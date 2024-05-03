package com.oviktor.controller.action.actions.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oviktor.controller.action.actions.AbstractAction;
import com.oviktor.dto.UpdateAppointmentDto;
import com.oviktor.dto.UserInfo;
import com.oviktor.entity.Appointment;
import com.oviktor.enums.AppointmentTypes;
import com.oviktor.enums.Roles;
import com.oviktor.service.AppointmentService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentActionTest extends AbstractAction {

    @Mock
    private AppointmentService appointmentService;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    AppointmentAction appointmentAction;


    @BeforeEach
    void setUp() {

        appointmentAction = new AppointmentAction(appointmentService);
        AppointmentTypes.CONSULTATION.setId(1L);
        AppointmentTypes.MEDICATION.setId(2L);
        AppointmentTypes.PROCEDURE.setId(3L);
        AppointmentTypes.OPERATION.setId(4L);

    }

    @Test
    void cancelAppointmentNoCookieProvidedTest() throws IOException {

        String expectedResult = "redirect";
        doReturn("123").when(request).getParameter("appointmentId");
        doNothing().when(appointmentService).cancelAppointment(123L);
        String actualResult = appointmentAction.cancelAppointment().execute(request, response);
        assertEquals(expectedResult, actualResult);
        // Verify that the appointmentService.cancelAppointment method was called with the correct argument
        verify(request, times(1)).getParameter("appointmentId");
        verify(appointmentService, times(1)).cancelAppointment(123L);
        verify(response, never()).sendRedirect("controller?action=success");

    }

    @ParameterizedTest
    @EnumSource(value = Roles.class, names = {"ADMIN", "DOCTOR", "NURSE", "PATIENT"})
    void cancelAppointmentCookieProvidedTest(Roles role) throws IOException {

        String expectedResult = "redirect";

        doReturn("123").when(request).getParameter("appointmentId");
        doNothing().when(appointmentService).cancelAppointment(123L);
        doReturn(buildCookie(123L, role)).when(request).getCookies();
        String actualResult = appointmentAction.cancelAppointment().execute(request, response);
        assertEquals(expectedResult, actualResult);

        verify(request, times(1)).getParameter("appointmentId");
        verify(appointmentService, times(1)).cancelAppointment(123L);
        verify(response, times(1)).sendRedirect("controller?action=success");
    }

    @ParameterizedTest
    @EnumSource(value = Roles.class, names = {"ADMIN", "DOCTOR", "NURSE", "PATIENT"})
    void showUpdateAppointmentPage(Roles role) throws IOException {
        String expectedResult = role.name().toLowerCase() + "/" + role.name().toLowerCase() + "_update_appointment_page.jsp";
        Appointment currentAppointment = Appointment
                .builder()
                .id(321L)
                .patientId(12L)
                .doctorId(13L)
                .appointmentType(AppointmentTypes.PROCEDURE)
                .appointmentDate(LocalDateTime.of(LocalDate.of(2023, 6, 25), LocalTime.of(13, 37)))
                .diagnosisId(44L)
                .canceled(false)
                .build();
        doReturn("321").when(request).getParameter("appointmentId");
        doReturn(currentAppointment).when(appointmentService).getAppointmentById(321L);
        doNothing().when(request).setAttribute("currentAppointment", currentAppointment);
        doReturn(buildCookie(123L, role)).when(request).getCookies();
        String actualResult = appointmentAction.showUpdateAppointmentPage().execute(request, response);
        assertEquals(expectedResult, actualResult);
        verify(request, times(1)).getParameter("appointmentId");
        verify(appointmentService, times(1)).getAppointmentById(321L);
        verify(request, times(1)).setAttribute("currentAppointment", currentAppointment);

    }

    @ParameterizedTest
    @EnumSource(value = Roles.class, names = {"ADMIN", "DOCTOR", "NURSE", "PATIENT"})
    void updateAppointmentTest(Roles role) throws IOException {

        String expectedResult = "redirect";
        doReturn("321").when(request).getParameter("appointmentId");
        doReturn("3").when(request).getParameter("appointmentType");
        doReturn("2023-03-05T12:00:00").when(request).getParameter("appointmentDateTime");
        doReturn(buildCookie(25L, role)).when(request).getCookies();
        doNothing().when(appointmentService).updateAppointment(any(UserInfo.class), any(UpdateAppointmentDto.class));
        String actualResult = appointmentAction.updateAppointment().execute(request, response);
        assertEquals(expectedResult, actualResult);
        verify(request, times(1)).getParameter("appointmentId");
        verify(request, times(1)).getParameter("appointmentType");
        verify(request, times(1)).getParameter("appointmentDateTime");
        verify(appointmentService,times(1)).updateAppointment(any(UserInfo.class), any(UpdateAppointmentDto.class));
        verify(response, times(1)).sendRedirect("controller?action=success");
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