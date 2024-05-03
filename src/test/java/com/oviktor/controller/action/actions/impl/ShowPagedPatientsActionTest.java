package com.oviktor.controller.action.actions.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oviktor.controller.action.Commands;
import com.oviktor.controller.action.actions.PagedPatientsAction;
import com.oviktor.dao.utils.Pagination;
import com.oviktor.dao.utils.Sorting;
import com.oviktor.dto.PagedPatientsDto;
import com.oviktor.dto.PatientDto;
import com.oviktor.dto.UserInfo;
import com.oviktor.enums.PatientsSortBy;
import com.oviktor.enums.Roles;
import com.oviktor.service.PatientService;
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
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowPagedPatientsActionTest {

    @Mock
    PatientService patientService;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;

    ShowPagedPatientsAction showPagedPatientsAction;

    @BeforeEach
    void setUp() {
        showPagedPatientsAction = new ShowPagedPatientsAction(patientService);

    }

    @ParameterizedTest
    @EnumSource(value = Roles.class, mode = EnumSource.Mode.MATCH_NONE)
    void showPagedPatientsPageByNameAscOrderTest(Roles role) throws IOException {
        PatientsSortBy sortBy = PatientsSortBy.NAME;
        String errorMessage = "You're not allowed to access this resource.";
        String expectedResult = "redirect";
        String expectedResultNotPatientRole = role.name().toLowerCase() + "/" + role.name().toLowerCase() + "_" + "get_all_patients.jsp";
        Sorting sorting = Sorting.ASC;
        List<PatientDto> patientDtos = Arrays.asList(null, null, null, null, null, null, null, null);
        PagedPatientsDto pagedPatientsDto = new PagedPatientsDto(patientDtos, 45, 10);
        Pagination pagination = Pagination.pageNum(45);
        doReturn("45").when(request).getParameter("pageNum");
        doReturn(sorting.toString()).when(request).getParameter("sorting");
        doReturn(sortBy.toString()).when(request).getParameter("sortBy");
        doNothing().when(request).setAttribute("patientsSorting", sorting.toString());
        doNothing().when(request).setAttribute("patientsSortBy", sortBy.toString());
        doReturn(pagedPatientsDto).when(patientService).getPatientsSortedByName(sorting,pagination);
        doNothing().when(request).setAttribute("pagedPatients", pagedPatientsDto);
        doReturn(buildCookie(25L, role)).when(request).getCookies();
        if (role.name().equals("PATIENT")) {
            doNothing().when(request).setAttribute("errorMessage", errorMessage);
            doNothing().when(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            String actualResultPatientRole = ShowPagedPatientsAction.showPagedPatientsPage().execute(request, response);
            assertEquals(expectedResult, actualResultPatientRole);
            verify(response).sendRedirect("controller?action=" + Commands.StatusCommands.FORBIDDEN);
        } else {
            String actualResultNotPatientRole = ShowPagedPatientsAction.showPagedPatientsPage().execute(request, response);
            assertEquals(expectedResultNotPatientRole, actualResultNotPatientRole);
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