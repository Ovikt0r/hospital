package com.oviktor.controller.action.actions.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oviktor.controller.action.Commands;
import com.oviktor.controller.action.actions.AbstractAction;
import com.oviktor.dto.CreateUserDto;
import com.oviktor.dto.DoctorDto;
import com.oviktor.dto.UserInfo;
import com.oviktor.enums.MedicineCategories;
import com.oviktor.enums.Roles;
import com.oviktor.service.MedicalStaffService;
import com.password4j.HashBuilder;
import com.password4j.Password;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorActionTest extends AbstractAction{

    @Mock
    MedicalStaffService medicalStaffService;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;


    DoctorAction doctorAction;

    @BeforeEach
    void setUp() {
        doctorAction = new DoctorAction(medicalStaffService);
        MedicineCategories.TRAUMATOLOGIST.setId(1L);
        MedicineCategories.SURGEON.setId(2L);
        MedicineCategories.PEDIATRICIAN.setId(3L);
        MedicineCategories.NEUROLOGISTS.setId(4L);
    }


    @ParameterizedTest
    @EnumSource(value = Roles.class, names = {"ADMIN", "DOCTOR", "NURSE", "PATIENT"})
    void createDoctorTest(Roles role) throws IOException {
        String expectedResult = "redirect";
        LocalDate birthDate = LocalDate.of(1971, 3, 19);
        String successErrorMessage = "Success.";
        String errorMessage = "You're not allowed to access this resource.";

        doReturn(buildCookie(65L, role)).when(request).getCookies();
        if (!role.name().equals("ADMIN")) {
            doNothing().when(request).setAttribute("errorMessage", errorMessage);
            doNothing().when(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            String actualResultNotAdminRole = doctorAction.createDoctor().execute(request, response);
            assertEquals(expectedResult, actualResultNotAdminRole);
            verify(response).sendRedirect("controller?action=" + Commands.StatusCommands.FORBIDDEN);
        } else {

            String pass = Password.hash("325678421").withBcrypt().getResult();
            CreateUserDto userDto = CreateUserDto.builder()
                    .firstName("Vasily")
                    .lastName("Portman")
                    .phone("+380974551245")
                    .email("portman@gmail.com")
                    .usersPassword(pass)
                    .dateOfBirth(birthDate)
                    .build();

            doReturn(userDto.getFirstName()).when(request).getParameter("firstName");
            doReturn(userDto.getLastName()).when(request).getParameter("lastName");
            doReturn(userDto.getPhone()).when(request).getParameter("phone");
            doReturn(userDto.getEmail()).when(request).getParameter("email");
            doReturn("325678421").when(request).getParameter("password");
            doReturn(userDto.getDateOfBirth().toString()).when(request).getParameter("birthday");
            doReturn("2").when(request).getParameter("categoryId");
            doNothing().when(medicalStaffService).createDoctor(userDto, MedicineCategories.SURGEON);
            doNothing().when(request).setAttribute("message", successErrorMessage);
            doNothing().when(response).setStatus(HttpServletResponse.SC_OK);
            String actualResultAdminRole = doctorAction.createDoctor().execute(request, response);
            assertEquals(expectedResult, actualResultAdminRole);
            verify(response).sendRedirect("controller?action=" + Commands.StatusCommands.SUCCESS);
        }
    }

    @ParameterizedTest
    @EnumSource(value = Roles.class, names = {"ADMIN", "DOCTOR", "NURSE", "PATIENT"})
    void showCreateDoctorPageTest(Roles role) throws IOException {
        String errorMessage = "You're not allowed to access this resource.";
        String expectedResultNotAdminRole = "redirect";
        String expectedResultAdminRole = AbstractAction.ADMIN_PREFIX+"create_doctor.jsp";

        doNothing().when(request).setAttribute("categories", MedicineCategories.getAll());
        doReturn(buildCookie(65L, role)).when(request).getCookies();
        if(!role.name().equals("ADMIN")) {
            doNothing().when(request).setAttribute("errorMessage", errorMessage);
            doNothing().when(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            String actualResultNotAdminRole = doctorAction.showCreateDoctorPage().execute(request, response);
            assertEquals(expectedResultNotAdminRole, actualResultNotAdminRole);
            verify(response).sendRedirect("controller?action=" + Commands.StatusCommands.FORBIDDEN);
        }
        else {
            String actualResultAdminRole = doctorAction.showCreateDoctorPage().execute(request, response);
            assertEquals(expectedResultAdminRole, actualResultAdminRole);
        }

    }

    @ParameterizedTest
    @EnumSource(value = Roles.class, names = {"ADMIN", "DOCTOR", "NURSE", "PATIENT"})
    void showDoctorCardPageTest(Roles role) throws JsonProcessingException {
        String expectedResult = role.name().toLowerCase()+"/"+role.name().toLowerCase()+"_"+"doctor_card.jsp";
        LocalDate birthDate = LocalDate.of(1971, 3, 19);
        Cookie[] cookie = buildCookie(25L, role);
        DoctorDto doctorDto = DoctorDto.builder()
                .id(25L)
                .firstName("Boris")
                .lastName("Meleshko")
                .phone("+38741546562")
                .email("borsi@jonson.com")
                .dateOfBirth(birthDate)
                .medicineCategory(MedicineCategories.TRAUMATOLOGIST)
                .build();
        doReturn("25").when(request).getParameter("doctorId");
        doReturn(cookie).when(request).getCookies();
        doReturn(doctorDto).when(medicalStaffService).getDoctorById(25L);
        doNothing().when(request).setAttribute("foundDoctor", doctorDto);
        String actualResult = doctorAction.showDoctorCardPage().execute(request,response);
        assertEquals(expectedResult,actualResult);

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