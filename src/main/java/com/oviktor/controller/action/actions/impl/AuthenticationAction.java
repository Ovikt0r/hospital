package com.oviktor.controller.action.actions.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oviktor.ApplicationContext;
import com.oviktor.controller.action.Actionable;
import com.oviktor.controller.action.Commands;
import com.oviktor.controller.action.actions.AbstractAction;
import com.oviktor.dto.*;
import com.oviktor.enums.Roles;
import com.oviktor.service.MedicalStaffService;
import com.oviktor.service.PatientService;
import com.oviktor.service.SecurityService;
import com.oviktor.utils.CookiesReader;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;

@Slf4j
public class AuthenticationAction extends AbstractAction {

    private static SecurityService securityService = ApplicationContext.getInstance().getSecurityService();
    private static MedicalStaffService medicalStaffService = ApplicationContext.getInstance().getMedicalStaffService();
    private static PatientService patientService = ApplicationContext.getInstance().getPatientService();
    private static ObjectMapper objectMapper = ApplicationContext.getInstance().getObjectMapper();

    AuthenticationAction(SecurityService securityService,MedicalStaffService medicalStaffService,PatientService patientService,ObjectMapper objectMapper ){
        AuthenticationAction.securityService = securityService;
        AuthenticationAction.medicalStaffService = medicalStaffService;
        AuthenticationAction.patientService = patientService;
        AuthenticationAction.objectMapper = objectMapper;
    }
    private static final String MAIN_PAGE = "menu.jsp";

    public static Actionable showLoginPage() {
       log.info("The login page will be show");
        return (request, response) -> LOGIN_PAGE;
    }

    public static Actionable login() {
        return (request, response) -> {
            String login = request.getParameter("login");
            String password = request.getParameter("password");
            if (login == null || password == null || login.isEmpty() || password.isEmpty()) {
                setAttributeToSession(request, "messageError", "login.wrong.empty");
                return sendRedirect(Commands.LOGIN_PAGE, response);
            }

            return securityService.checkCredentialsAndReturnUserInfo(login, password)
                    .map(userInfo -> {
                        String userCredentialsJson;
                        try {
                            userCredentialsJson = objectMapper.writeValueAsString(userInfo);
                        } catch (JsonProcessingException e) {
                            log.error("In the process of JSON serialization there was some problem. More details:", e);
                            return internalServerError("Something went wrong. Please, try again later.", request, response);
                        }
                        String encryptedUserCredentials = Base64.getEncoder().encodeToString(userCredentialsJson.getBytes());
                        Cookie cookie = new Cookie("user-info", encryptedUserCredentials);
                        response.addCookie(cookie);

                        setAttributeToSession(request, "user", userInfo);

                        UserNameDto currentUser = securityService.getUserNameById(userInfo.id());
                        setAttributeToSession(request, "currentUserLastName", currentUser.lastName());
                        setAttributeToSession(request, "currentUserFirstName", currentUser.firstName());
                        removeAttributeFromSession(request, "messageError");

                        return redirectToInitialPage(response);
                    })
                    .orElseGet(() -> {
                        setAttributeToSession(request, "messageError", "login.wrong.credentials");
                        return sendRedirect(Commands.LOGIN_PAGE, response);
                    });
        };
    }

    private static String redirectToInitialPage(HttpServletResponse response) {
        return sendRedirect(Commands.MAIN, response);
    }

    public static Actionable showMainPage() {
        return (request, response) -> getRole(request)
                .map((Roles role) -> getInitialPage(role, request))
                .orElseGet(() -> "login");
    }

    private static String getInitialPage(Roles role, HttpServletRequest request) {
        UserInfo currentUserInfo = getCurrentUserInfo(request);
        return switch (role) {
            case PATIENT -> {
                PatientDto foundPatient = patientService.getPatientById(currentUserInfo.id());
                request.setAttribute("foundPatient", foundPatient);
                yield PATIENT_PREFIX + MAIN_PAGE;
            }
            case DOCTOR -> {
                DoctorDto foundDoctor = medicalStaffService.getDoctorById(currentUserInfo.id());
                request.setAttribute("foundDoctor", foundDoctor);
                setAttributeToSession(request, "currentUserLastName", foundDoctor.getLastName());
                setAttributeToSession(request, "currentUserFirstName", foundDoctor.getFirstName());
                yield DOCTOR_PREFIX + MAIN_PAGE;
            }
            case NURSE -> {
                NurseDto foundNurse = medicalStaffService.getNurseById(currentUserInfo.id());
                request.setAttribute("foundNurse", foundNurse);
                yield NURSE_PREFIX + MAIN_PAGE;
            }
            case ADMIN -> {
                AdminDto foundAdmin = medicalStaffService.getAdminById(currentUserInfo.id());
                request.setAttribute("foundAdmin", foundAdmin);
                yield ADMIN_PREFIX + MAIN_PAGE;
            }
        };
    }

    public static Actionable logout() {
        return (request, response) -> {
            CookiesReader.readCookie("user-info", request)
                    .ifPresent((cookie) -> {
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    });
            removeAttributeFromSession(request, "user-info");
            removeAttributeFromSession(request, "currentUserLastName");
            removeAttributeFromSession(request, "currentUserFirstName");

            return sendRedirect(Commands.LOGIN_PAGE, response);
        };
    }
}
