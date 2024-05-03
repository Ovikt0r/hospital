package com.oviktor.controller;

import com.oviktor.controller.action.Commands;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.oviktor.controller.action.Commands.StatusCommands.*;

public class StatusPagesRedirector {

     public static String unauthorized(HttpServletRequest request, HttpServletResponse response) {
        return unauthorized("Wrong credentials", request, response);
    }

     public static String unauthorized(String errorMessage, HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("errorMessage", errorMessage);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return sendRedirect(Commands.LOGIN, response);
    }

    public static String internalServerError(HttpServletRequest request, HttpServletResponse response) {
        return internalServerError("Something went wrong. Please, try again later.", request, response);
    }

    public static String internalServerError(String errorMessage, HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("errorMessage", errorMessage);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return sendRedirect(ERROR, response);
    }

    public static String forbidden(HttpServletRequest request, HttpServletResponse response) {
        return forbidden("You're not allowed to access this resource.", request, response);
    }

    public static String forbidden(String errorMessage, HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("errorMessage", errorMessage);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return sendRedirect(FORBIDDEN, response);
    }

    public static String notFound(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return sendRedirect(NOT_FOUND, response);
    }

    public static String success(HttpServletRequest request, HttpServletResponse response) {
        return success("Success.", request, response);
    }

    public static String success(String errorMessage, HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("message", errorMessage);
        response.setStatus(HttpServletResponse.SC_OK);
        return sendRedirect(SUCCESS, response);
    }

    private static String sendRedirect(String command, HttpServletResponse response) {
        try {
            response.sendRedirect("controller?action=" + command);
            return REDIRECT;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
