package com.oviktor.controller.action.actions.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oviktor.controller.action.Commands;
import com.oviktor.dto.DoctorDto;
import com.oviktor.dto.UserInfo;
import com.oviktor.dto.UserNameDto;
import com.oviktor.enums.MedicineCategories;
import com.oviktor.enums.Roles;
import com.oviktor.service.MedicalStaffService;
import com.oviktor.service.PatientService;
import com.oviktor.service.SecurityService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.control.MappingControl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AuthenticationActionTest {

    @Mock
    SecurityService securityService;
    @Mock
    MedicalStaffService medicalStaffService;
    @Mock
    PatientService patientService;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    HttpServletRequest request;
    @Mock
    HttpSession session;
    @Mock
    HttpServletResponse response;
    AuthenticationAction authenticationAction;

    @BeforeEach
    void setUp() {
        authenticationAction = new AuthenticationAction(
                securityService, medicalStaffService, patientService, objectMapper
        );

    }

    @Test
    void showLoginPageTest() {
        String expectedResult = "login.jsp";
        String actualResult = authenticationAction.showLoginPage().execute(request, response);
        assertEquals(expectedResult, actualResult);
    }


    @SneakyThrows
    @ParameterizedTest
    @EnumSource(value = Roles.class, names = {"ADMIN", "DOCTOR", "NURSE", "PATIENT"})
    void loginTest(Roles role) {
        String expectedResult = "redirect";
        String login = "some@mail.com";
        String password = "442sdf475gpmmd9kjhh234kbn7";
        UserInfo userInfo = new UserInfo(33L, role.name());
        UserNameDto userNameDto = new UserNameDto("Пущенренко", "Мар'яна");
        ObjectMapper objectMapper1 = new ObjectMapper();
        Optional<UserInfo> userInfoOptional = Optional.of(userInfo);
        String userCredentialsJson = objectMapper1.writeValueAsString(userInfo);
        doReturn(login).when(request).getParameter("login");
        doReturn(password).when(request).getParameter("password");
        doReturn(userInfoOptional).when(securityService).checkCredentialsAndReturnUserInfo(login, password);
        doReturn(userCredentialsJson).when(objectMapper).writeValueAsString(userInfo);
        doReturn(session).when(request).getSession();
        doNothing().when(session).setAttribute("user", userInfo);
        doReturn(userNameDto).when(securityService).getUserNameById(userInfo.id());
        doNothing().when(session).setAttribute("currentUserLastName", userNameDto.lastName());
        doNothing().when(session).setAttribute("currentUserFirstName", userNameDto.firstName());
        doNothing().when(session).removeAttribute("messageError");
        String actualResult = authenticationAction.login().execute(request, response);
        assertEquals(expectedResult, actualResult);
        verify(request).getParameter("login");
        verify(request).getParameter("password");
        verify(securityService).checkCredentialsAndReturnUserInfo(login, password);
        verify(objectMapper).writeValueAsString(userInfo);
        verify(request, times(4)).getSession();
        verify(session).setAttribute("user", userInfo);
        verify(securityService).getUserNameById(userInfo.id());
        verify(session).setAttribute("currentUserLastName", userNameDto.lastName());
        verify(session).setAttribute("currentUserFirstName", userNameDto.firstName());
        verify(session).removeAttribute("messageError");
        verify(response, times(1)).sendRedirect("controller?action=mainPage");

    }


    @SneakyThrows
    @ParameterizedTest
    @EnumSource(value = Roles.class, names = {"ADMIN", "DOCTOR", "NURSE", "PATIENT"})
    void showMainPageTest(Roles role) {

        String expectedResult = role.name().toLowerCase() + "/" + role.name().toLowerCase() + "_" + "menu.jsp";
        UserInfo userInfo = new UserInfo(61L, role.name());
        doReturn(buildCookie(61L, role)).when(request).getCookies();
        if (userInfo.role() == "DOCTOR") {
            DoctorDto doctorDto = DoctorDto.builder()
                    .id(61L)
                    .firstName("Venice")
                    .lastName("Gordon")
                    .dateOfBirth(LocalDate.of(1981, 8, 28))
                    .email("prodoc@proton.mail")
                    .numOfPatients(43)
                    .medicineCategory(MedicineCategories.NEUROLOGISTS)
                    .phone("+8452267723")
                    .build();
            doReturn(doctorDto).when(medicalStaffService).getDoctorById(doctorDto.getId());
            doNothing().when(request).setAttribute("foundDoctor", doctorDto);
            doReturn(session).when(request).getSession();
            doNothing().when(session).setAttribute("currentUserLastName", doctorDto.getLastName());
            doNothing().when(session).setAttribute("currentUserFirstName", doctorDto.getFirstName());
        }
        String actualResult = authenticationAction.showMainPage().execute(request, response);
        assertEquals(expectedResult, actualResult);
    }

    @SneakyThrows
    @ParameterizedTest
    @EnumSource(value = Roles.class, names = {"ADMIN", "DOCTOR", "NURSE", "PATIENT"})
    void logoutTest(Roles role) {
        String expectedResult = "redirect";
        Cookie cookie = Arrays.stream(
                buildCookie(65L, role))
                .findAny()
                .get();
        cookie.setMaxAge(0);
        doReturn(buildCookie(65L, role)).when(request).getCookies();
        doNothing().when(response).addCookie(cookie);
        doReturn(session).when(request).getSession();
        doNothing().when(session).removeAttribute("user-info");
        doNothing().when(session).removeAttribute("currentUserLastName");
        doNothing().when(session).removeAttribute("currentUserFirstName");
        doNothing().when(response).sendRedirect("controller?action=" + Commands.LOGIN_PAGE);
        String actualResult = authenticationAction.logout().execute(request,response);
        assertEquals(expectedResult,actualResult);
        verify(response, times(1)).sendRedirect("controller?action=loginPage");

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