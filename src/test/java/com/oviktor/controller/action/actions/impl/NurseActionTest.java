package com.oviktor.controller.action.actions.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oviktor.controller.action.Commands;
import com.oviktor.dto.NurseDto;
import com.oviktor.dto.UserInfo;
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
import java.time.LocalDate;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NurseActionTest {
    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    MedicalStaffService medicalStaffService;
    NurseAction nurseAction;

    @BeforeEach
    void setUp() {
        nurseAction = new NurseAction(medicalStaffService);
    }

    @ParameterizedTest
    @EnumSource(value = Roles.class, mode = EnumSource.Mode.MATCH_NONE)
    void showNurseCardTest(Roles role) throws IOException {
        LocalDate birthDay = LocalDate.of(1989, 5, 14);
        NurseDto foundNurse = NurseDto.builder()
                .id(61L)
                .firstName("Lisa")
                .lastName("Vuichich")
                .email("nurse@hospital.com")
                .phone("+6541789455")
                .dateOfBirth(birthDay)
                .build();
        doReturn("61").when(request).getParameter("nurseId");
        doReturn(foundNurse).when(medicalStaffService).getNurseById(61L);
        doNothing().when(request).setAttribute("foundNurse", foundNurse);
        doReturn(buildCookie(25L, role)).when(request).getCookies();
        if (role.name().equals("PATIENT")) {
            String expectedResultForbidden = "redirect";
            String errorMessage = "You're not allowed to access this resource.";
            doNothing().when(request).setAttribute("errorMessage", errorMessage);
            doNothing().when(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
            String actualResultPatientRole = NurseAction.showNurseCard().execute(request, response);
            assertEquals(expectedResultForbidden, actualResultPatientRole);
            verify(response).sendRedirect("controller?action=" + Commands.StatusCommands.FORBIDDEN);
        } else {
            String expectedResultSuccess = role.name().toLowerCase() + "/" + role.name().toLowerCase() + "_" + "nurse_card.jsp";
            String actualResultSuccess = NurseAction.showNurseCard().execute(request, response);
            assertEquals(expectedResultSuccess, actualResultSuccess);
        }


    }

    @Test
    void createNurseTest() {
    }

    @Test
    void showCreateNursePageTest() {
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