package com.oviktor.controller.action.actions.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oviktor.controller.action.Commands;
import com.oviktor.dto.MakeAppointmentDto;
import com.oviktor.dto.UserInfo;
import com.oviktor.enums.AppointmentTypes;
import com.oviktor.enums.Roles;
import com.oviktor.service.AppointmentService;
import com.oviktor.service.MedicalStaffService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAppointmentActionTest {
    @Mock
    HttpServletResponse response;
    @Mock
    HttpServletRequest request;
    @Mock
    MedicalStaffService medicalStaffService;
    @Mock
    AppointmentService appointmentService;
    @Mock
    PatientService patientService;
    CreateAppointmentAction createAppointmentAction;

    @BeforeEach
    void setUp() {
        createAppointmentAction =
                new CreateAppointmentAction(appointmentService,medicalStaffService,patientService);
        AppointmentTypes.CONSULTATION.setId(1L);
        AppointmentTypes.MEDICATION.setId(2L);
        AppointmentTypes.PROCEDURE.setId(3L);
        AppointmentTypes.OPERATION.setId(4L);
    }

    @ParameterizedTest
    @EnumSource(value = Roles.class, names = {"ADMIN", "DOCTOR", "NURSE", "PATIENT"})
    void createAppointmentTest(Roles role) throws IOException {
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(2023,6,25), LocalTime.of(14,15));
        UserInfo userInfo = new UserInfo(52L,role.name());
        MakeAppointmentDto makeAppointmentDto = MakeAppointmentDto.builder()
                .doctorId(45L)
                .patientId(11L)
                .appointmentType(AppointmentTypes.OPERATION)
                .appointmentDateTime(localDateTime)
                .build();
        doReturn(buildCookie(52L, role)).when(request).getCookies();
        doReturn("11").when(request).getParameter("patientId");
        doReturn("45").when(request).getParameter("doctorId");
        doReturn("4").when(request).getParameter("appointmentTypeId");
        doReturn(localDateTime.toString()).when(request).getParameter("appointmentDateTime");
        doNothing().when(appointmentService).makeAppointment(userInfo,makeAppointmentDto);
        if (role.name().equals("PATIENT")) {
            String expectedResultForbidden = "redirect";
            String errorMessage = "You're not allowed to access this resource.";
            doNothing().when(request).setAttribute("errorMessage", errorMessage);
            doNothing().when(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            String actualResultForbidden = createAppointmentAction.createAppointment().execute(request, response);
            assertEquals(expectedResultForbidden, actualResultForbidden);
            verify(response).sendRedirect("controller?action=" + Commands.StatusCommands.FORBIDDEN);
        } else {
            String expectedResultSuccess = "redirect";
            String actualResultSuccess =  createAppointmentAction.createAppointment().execute(request, response);
            assertEquals(expectedResultSuccess, actualResultSuccess);
            verify(response, times(1)).sendRedirect("controller?action=success");
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