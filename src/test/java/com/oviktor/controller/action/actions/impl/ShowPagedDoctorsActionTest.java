package com.oviktor.controller.action.actions.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oviktor.controller.action.Commands;
import com.oviktor.dao.utils.Pagination;
import com.oviktor.dao.utils.Sorting;
import com.oviktor.dto.DoctorDto;
import com.oviktor.dto.PagedDoctorsDto;
import com.oviktor.dto.UserInfo;
import com.oviktor.enums.DoctorsSortBy;
import com.oviktor.enums.MedicineCategories;
import com.oviktor.enums.Roles;
import com.oviktor.service.MedicalStaffService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowPagedDoctorsActionTest {

    @Mock
    HttpServletResponse response;
    @Mock
    HttpServletRequest request;
    @Mock
    MedicalStaffService medicalStaffService;
    ShowPagedDoctorsAction showPagedDoctorsAction;

    @BeforeEach
    void setUp() {
        MedicineCategories.NEUROLOGISTS.setId(1L);
        MedicineCategories.PEDIATRICIAN.setId(2L);
        MedicineCategories.SURGEON.setId(3L);
        MedicineCategories.TRAUMATOLOGIST.setId(4L);
        showPagedDoctorsAction = new ShowPagedDoctorsAction(medicalStaffService);
    }

    @ParameterizedTest
    @EnumSource(value = Roles.class, names = {"ADMIN", "DOCTOR", "NURSE", "PATIENT"})
    void showPagedDoctorsPageTest(Roles role) throws IOException {
        Sorting sorting = Sorting.DESC;
        int pageNum = 20;
        DoctorsSortBy sortBy = DoctorsSortBy.NUM;
        MedicineCategories filterByCategory = MedicineCategories.NEUROLOGISTS;
        List<DoctorDto> doctors = Arrays.asList(null,null,null,null,null,null);
        PagedDoctorsDto pagedDoctorsDto = new PagedDoctorsDto(doctors,20,15);
        doReturn("20").when(request).getParameter("pageNum");
        doReturn(Sorting.DESC.toString()).when(request).getParameter("sorting");
        doReturn(DoctorsSortBy.NUM.toString()).when(request).getParameter("sortBy");
        doReturn("1").when(request).getParameter("categoryId");
        doNothing().when(request).setAttribute("sorting", sorting.toString());
        doNothing().when(request).setAttribute("sortBy", sortBy.toString());
        doNothing().when(request).setAttribute("categories", MedicineCategories.getAll());
        doNothing().when(request).setAttribute("categoryId", filterByCategory);
        doReturn(pagedDoctorsDto).when(medicalStaffService).getDoctorsByNumberOfPatients(sorting, filterByCategory, Pagination.pageNum(pageNum));
        doNothing().when(request).setAttribute("pagedDoctors", pagedDoctorsDto);
        doReturn(buildCookie(69L, role)).when(request).getCookies();
        if (role.name().equals("PATIENT") || role.name().equals("DOCTOR")) {
            String expectedResultForbidden = "redirect";
            String errorMessage = "You're not allowed to access this resource.";
            doNothing().when(request).setAttribute("errorMessage", errorMessage);
            doNothing().when(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            String actualResultForbidden = showPagedDoctorsAction.showPagedDoctorsPage().execute(request, response);
            assertEquals(expectedResultForbidden, actualResultForbidden);
            verify(response).sendRedirect("controller?action=" + Commands.StatusCommands.FORBIDDEN);
        } else {
            String expectedResultSuccess = role.name().toLowerCase() + "/" + role.name().toLowerCase() + "_" + "get_all_doctors.jsp";
            String actualResultSuccess = showPagedDoctorsAction.showPagedDoctorsPage().execute(request, response);
            assertEquals(expectedResultSuccess, actualResultSuccess);
        }


    }

    @SneakyThrows
    private Cookie[] buildCookie(long id, Roles role) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserInfo userInfo = new UserInfo(id, role.name());
        String userCredentialsJson = objectMapper.writeValueAsString(userInfo);
        String encryptedUserCredentials = Base64.getEncoder().encodeToString(userCredentialsJson.getBytes());
        return new Cookie[]{new Cookie("user-info", encryptedUserCredentials)};
    }
}