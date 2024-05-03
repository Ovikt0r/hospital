package com.oviktor.controller.action.actions.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oviktor.controller.action.Commands;
import com.oviktor.dto.TypesDto;
import com.oviktor.dto.UserInfo;
import com.oviktor.entity.Diagnosis;
import com.oviktor.enums.DiagnosisTypes;
import com.oviktor.enums.Roles;
import com.oviktor.service.DiagnosisService;
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
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiagnosisActionTest {

    @Mock
    HttpServletResponse response;
    @Mock
    HttpServletRequest request;
    @Mock
    DiagnosisService diagnosisService;
    DiagnosisAction diagnosisAction;

    @BeforeEach
    void setUp() {
        diagnosisAction = new DiagnosisAction(diagnosisService);
        DiagnosisTypes.SOUND_HEALING.setId(1L);
        DiagnosisTypes.TREATING.setId(2L);
        DiagnosisTypes.MEDITATION.setId(3L);
        DiagnosisTypes.CLINICAL_EXAMINATION.setId(4L);
        DiagnosisTypes.INTENSIVE_TREATING.setId(5L);
        DiagnosisTypes.TREATING_IS_FINISHED.setId(6L);
        DiagnosisTypes.OPERATION.setId(7L);
    }

    @ParameterizedTest
    @EnumSource(value = Roles.class, names = {"ADMIN", "DOCTOR", "NURSE", "PATIENT"})
    void showSetDiagnosisPageTest(Roles role) throws IOException {
        long appointmentId = 25L;
        String expectedResultForbidden = "redirect";
        String errorMessage = "You're not allowed to access this resource.";
        doReturn("25").when(request).getParameter("appointmentId");
        doNothing().when(request).setAttribute("appointmentId", appointmentId);
        doNothing().when(request).setAttribute("diagnosisOptions", DiagnosisTypes.getAll());
        doReturn(buildCookie(69L, role)).when(request).getCookies();
        if (role.name().equals("PATIENT") || role.name().equals("NURSE")) {
            doNothing().when(request).setAttribute("errorMessage", errorMessage);
            doNothing().when(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            String actualResultForbidden = diagnosisAction.showSetDiagnosisPage().execute(request, response);
            assertEquals(expectedResultForbidden, actualResultForbidden);
            verify(response).sendRedirect("controller?action=" + Commands.StatusCommands.FORBIDDEN);
        } else {
            String expectedResultSuccess = role.name().toLowerCase() + "/" + role.name().toLowerCase() + "_" + "set_diagnosis_page.jsp";
            String actualResultSuccess = diagnosisAction.showSetDiagnosisPage().execute(request, response);
            assertEquals(expectedResultSuccess, actualResultSuccess);
        }
    }

    @ParameterizedTest
    @EnumSource(value = Roles.class, names = {"ADMIN", "DOCTOR", "NURSE", "PATIENT"})
    void setDiagnosisTest(Roles role) throws IOException {
        String diagnosisText = "Diagnosis text description";
        DiagnosisTypes diagnosisType = DiagnosisTypes.TREATING;
        doReturn(diagnosisText).when(request).getParameter("text");
        doReturn(diagnosisType.name()).when(request).getParameter("diagnosisType");
        doReturn("49").when(request).getParameter("appointmentId");
        doReturn(buildCookie(69L, role)).when(request).getCookies();
        UserInfo userInfo = new UserInfo(69L, role.name());
        doNothing().when(diagnosisService).diagnose(userInfo, 49L, diagnosisText, diagnosisType);
        if (role.name().equals("PATIENT") || role.name().equals("NURSE")) {
            String expectedResultForbidden = "redirect";
            String errorMessage = "You're not allowed to access this resource.";
            doNothing().when(request).setAttribute("errorMessage", errorMessage);
            doNothing().when(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            String actualResultForbidden = diagnosisAction.setDiagnosis().execute(request, response);
            assertEquals(expectedResultForbidden, actualResultForbidden);
            verify(response).sendRedirect("controller?action=" + Commands.StatusCommands.FORBIDDEN);
        } else {
            String expectedResultSuccess = "redirect";
            String actualResultSuccess = diagnosisAction.setDiagnosis().execute(request, response);
            assertEquals(expectedResultSuccess, actualResultSuccess);
            verify(response, times(1)).sendRedirect("controller?action=success");
        }

    }

    @ParameterizedTest
    @EnumSource(value = Roles.class, names = {"ADMIN", "DOCTOR", "NURSE", "PATIENT"})
    void showUpdateDiagnosisPageTest(Roles role) throws IOException {
        Diagnosis currentDiagnosis = Diagnosis.builder()
                .id(99L)
                .text("Some text description")
                .diagnosisType(DiagnosisTypes.CLINICAL_EXAMINATION)
                .build();
        TypesDto currentDiagnosisType = new TypesDto(currentDiagnosis.getDiagnosisType().getId(), currentDiagnosis.getDiagnosisType().name());
        List<TypesDto> diagnosisOptions = DiagnosisTypes.getAll();
        doReturn("99").when(request).getParameter("diagnosisId");
        doReturn(currentDiagnosis).when(diagnosisService).getDiagnosisById(currentDiagnosis.getId());
        doNothing().when(request).setAttribute("currentDiagnosis", currentDiagnosis);
        doNothing().when(request).setAttribute("selectedType", currentDiagnosisType);
        doNothing().when(request).setAttribute("diagnosisOptions", diagnosisOptions);
        diagnosisOptions.remove(currentDiagnosisType);
        doReturn(buildCookie(69L, role)).when(request).getCookies();
        if (role.name().equals("PATIENT") || role.name().equals("NURSE")) {
            String expectedResultForbidden = "redirect";
            String errorMessage = "You're not allowed to access this resource.";
            doNothing().when(request).setAttribute("errorMessage", errorMessage);
            doNothing().when(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            String actualResultForbidden = diagnosisAction.showUpdateDiagnosisPage().execute(request, response);
            assertEquals(expectedResultForbidden, actualResultForbidden);
            verify(response).sendRedirect("controller?action=" + Commands.StatusCommands.FORBIDDEN);
        } else {
            String expectedResultSuccess = role.name().toLowerCase() + "/" + role.name().toLowerCase() + "_" + "update_diagnosis_page.jsp";
            String actualResultSuccess = diagnosisAction.showUpdateDiagnosisPage().execute(request, response);
            assertEquals(expectedResultSuccess, actualResultSuccess);
        }
    }

    @ParameterizedTest
    @EnumSource(value = Roles.class, names = {"ADMIN", "DOCTOR", "NURSE", "PATIENT"})
    void updateDiagnosisTest(Roles role) throws IOException {
        doReturn("88").when(request).getParameter("diagnosisId");
        doReturn("some text").when(request).getParameter("text");
        doReturn("3").when(request).getParameter("diagnosisType");
        doNothing().when(diagnosisService).updateDiagnosis(88L, "some text", DiagnosisTypes.MEDITATION);
        doReturn(buildCookie(69L, role)).when(request).getCookies();
        if (role.name().equals("PATIENT") || role.name().equals("NURSE")) {
            String expectedResultForbidden = "redirect";
            String errorMessage = "You're not allowed to access this resource.";
            doNothing().when(request).setAttribute("errorMessage", errorMessage);
            doNothing().when(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            String actualResultForbidden = diagnosisAction.updateDiagnosis().execute(request, response);
            assertEquals(expectedResultForbidden, actualResultForbidden);
            verify(response).sendRedirect("controller?action=" + Commands.StatusCommands.FORBIDDEN);
        } else {
            String expectedResultSuccess = "redirect";
            String actualResultSuccess = diagnosisAction.updateDiagnosis().execute(request, response);
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