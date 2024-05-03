package com.oviktor.controller.action.actions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oviktor.ApplicationContext;
import com.oviktor.controller.action.Commands;
import com.oviktor.dto.UserInfo;
import com.oviktor.enums.Roles;
import com.oviktor.utils.CookiesReader;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Slf4j
public abstract class AbstractAction {

    protected static final String ADMIN_PREFIX = "admin/admin_";
    protected static final String DOCTOR_PREFIX = "doctor/doctor_";
    protected static final String NURSE_PREFIX = "nurse/nurse_";
    protected static final String PATIENT_PREFIX = "patient/patient_";

    protected static final String SUCCESS_PAGE = "status/success.jsp";
    protected static final String FORBIDDEN_PAGE = "status/forbidden.jsp";
    protected static final String NOT_FOUND_PAGE = "status/not_found.jsp";
    protected static final String ERROR_PAGE = "status/error.jsp";

    protected static final String LOGIN_PAGE = "login.jsp";

    private static final ObjectMapper objectMapper = ApplicationContext.getInstance().getObjectMapper();

    protected static Optional<Roles> getRole(HttpServletRequest request) {
        return CookiesReader.readCookie("user-info", request)
                .map(cookie -> new String(Base64.getDecoder().decode(cookie.getValue())))
                .map(value -> {
                    try {
                        return objectMapper.readValue(value, UserInfo.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(userInfo -> Roles.valueOf(userInfo.role()));
    }

    protected static UserInfo getCurrentUserInfo(HttpServletRequest request) {
        return CookiesReader.readCookie("user-info", request)
                .map(cookie -> new String(Base64.getDecoder().decode(cookie.getValue())))
                .map(value -> {
                    try {
                        return objectMapper.readValue(value, UserInfo.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow();
    }

    protected static void setAttributeToSession(HttpServletRequest request, String attributeName, Object attributeValue) {
        HttpSession session = getSession(request);
        session.setAttribute(attributeName, attributeValue);
    }

    protected static Optional<Object> getAttributeFromSession(HttpServletRequest request, String attributeName) {
        HttpSession session = getSession(request);
        return Optional.ofNullable(session.getAttribute(attributeName));
    }

    protected static void removeAttributeFromSession(HttpServletRequest request, String attributeName) {
        HttpSession session = getSession(request);
        session.removeAttribute(attributeName);
    }

    private static HttpSession getSession(HttpServletRequest request) {
        return request.getSession();
    }

    protected static String internalServerError(HttpServletRequest request, HttpServletResponse response) {
        log.error("Some internal server error was happened");
        return internalServerError("Something went wrong. Please, try again later.", request, response);
    }

    protected static String internalServerError(String errorMessage, HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("errorMessage", errorMessage);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return sendRedirect(Commands.StatusCommands.ERROR, response);
    }

    protected static String forbidden(HttpServletRequest request, HttpServletResponse response) {
        log.warn("This action forbidden for this role");
        return forbidden("You're not allowed to access this resource.", request, response);
    }

    protected static String forbidden(String errorMessage, HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("errorMessage", errorMessage);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return sendRedirect(Commands.StatusCommands.FORBIDDEN, response);
    }

    protected static String success(HttpServletRequest request, HttpServletResponse response) {
        return success("Success.", request, response);
    }

    protected static String success(String errorMessage, HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("message", errorMessage);
        log.trace("The request assigned the attribute with name 'message' and value {}", errorMessage);
        response.setStatus(HttpServletResponse.SC_OK);
        log.trace("The response got status SC_OK with code 200");
        return sendRedirect(Commands.StatusCommands.SUCCESS, response);
    }

    protected static String sendRedirect(String command, HttpServletResponse response) {
        try {
            response.sendRedirect("controller?action=" + command);
            log.info("Redirection with action {} was happened", command);
            return Commands.StatusCommands.REDIRECT;
        } catch (IOException e) {
            log.error("Redirection with action {} failed", command);
            throw new RuntimeException(e);
        }
    }

    protected static String excludePatient(String page, HttpServletRequest request, HttpServletResponse response) {
        return getRole(request)
                .map(roles -> switch (roles) {
                    case PATIENT -> forbidden(request, response);
                    case DOCTOR -> DOCTOR_PREFIX + page;
                    case NURSE -> NURSE_PREFIX + page;
                    case ADMIN -> ADMIN_PREFIX + page;
                }).orElseGet(() -> internalServerError(request, response));
    }

    protected static String successExcludingPatient(HttpServletRequest request, HttpServletResponse response) {
        return getRole(request)
                .map(roles -> switch (roles) {
                    case PATIENT -> forbidden(request, response);
                    case DOCTOR, NURSE, ADMIN -> success(request, response);
                }).orElseGet(() -> internalServerError(request, response));
    }

    protected static String successExcludingPatientAndNurse(HttpServletRequest request, HttpServletResponse response) {
        return getRole(request)
                .map(roles -> switch (roles) {
                    case PATIENT, NURSE -> forbidden(request, response);
                    case DOCTOR, ADMIN -> {
                        log.info("Successful execution !");
                        yield success(request, response);
                    }
                }).orElseGet(() -> internalServerError(request, response));
    }

    protected static String excludePatientAndNurse(String page, HttpServletRequest request, HttpServletResponse response) {
        return getRole(request)
                .map(roles -> switch (roles) {
                    case PATIENT, NURSE -> forbidden(request, response);
                    case DOCTOR -> DOCTOR_PREFIX + page;
                    case ADMIN -> ADMIN_PREFIX + page;
                }).orElseGet(() -> internalServerError(request, response));
    }

    protected static String excludePatientAndDoctor(String page, HttpServletRequest request, HttpServletResponse response) {
        return getRole(request)
                .map(roles -> switch (roles) {
                    case PATIENT, DOCTOR -> forbidden(request, response);
                    case NURSE -> NURSE_PREFIX + page;
                    case ADMIN -> ADMIN_PREFIX + page;
                }).orElseGet(() -> internalServerError(request, response));
    }

    protected static String excludeNone(String page, HttpServletRequest request, HttpServletResponse response) {
        return getRole(request)
                .map(roles -> switch (roles) {
                    case PATIENT -> PATIENT_PREFIX + page;
                    case NURSE -> NURSE_PREFIX + page;
                    case DOCTOR -> DOCTOR_PREFIX + page;
                    case ADMIN -> ADMIN_PREFIX + page;
                }).orElseGet(() -> internalServerError(request, response));
    }

    protected static String includeAdmin(String page, HttpServletRequest request, HttpServletResponse response) {
        return getRole(request)
                .map(roles -> switch (roles) {
                    case ADMIN -> {
                        log.info("Opening {} page for admin role", page);
                        yield ADMIN_PREFIX + page;
                    }
                    case PATIENT, NURSE, DOCTOR -> forbidden(request, response);
                }).orElseGet(() -> internalServerError(request, response));
    }

    protected static String includeAdminAndNurse(String page, HttpServletRequest request, HttpServletResponse response) {
        return getRole(request)
                .map(roles -> switch (roles) {
                    case ADMIN -> {
                        log.info("Opening {} page for admin role", page);
                        yield ADMIN_PREFIX + page;
                    }
                    case NURSE -> {
                        log.info("Opening {} page for nurse role", page);
                        yield NURSE_PREFIX + page;
                    }
                    case PATIENT, DOCTOR -> forbidden(request, response);
                }).orElseGet(() -> internalServerError(request, response));
    }
}
