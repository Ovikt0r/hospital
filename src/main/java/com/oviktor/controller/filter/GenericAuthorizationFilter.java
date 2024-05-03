package com.oviktor.controller.filter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oviktor.ApplicationContext;
import com.oviktor.controller.action.Commands;
import com.oviktor.dto.UserInfo;
import com.oviktor.enums.Roles;
import com.oviktor.utils.CookiesReader;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.oviktor.controller.StatusPagesRedirector.*;
@Slf4j
public abstract class GenericAuthorizationFilter implements Filter {

    private static final ObjectMapper objectMapper = ApplicationContext.getInstance().getObjectMapper();

    private final static List<String> statusCommands = Commands.getStatusCommands();


    protected void proceedRequest(List<String> allowedCommands, Roles role, ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        Optional<Roles> currentUserRole = getRole(httpRequest);
        if (currentUserRole.isPresent() && !currentUserRole.get().equals(role)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (httpRequest.getParameter("action") != null &&
                (httpRequest.getParameter("action").equals(Commands.LOGIN) ||
                        httpRequest.getParameter("action").equals(Commands.LOGIN_PAGE))) {
            filterChain.doFilter(request, response);
            return;
        }

        if (currentUserRole.isEmpty()) {
            unauthorized(httpRequest, httpResponse);
            return;
        }

        String action = httpRequest.getParameter("action");
        if (action == null) {
            internalServerError(httpRequest, httpResponse);
            return;
        }
        if (!anyMatch(allowedCommands, action)) {
            forbidden(httpRequest, httpResponse);
            return;
        }
        filterChain.doFilter(request, response);
    }

    protected static Optional<Roles> getRole(HttpServletRequest request) {
        return CookiesReader.readCookie("user-info", request)
                .map(cookie -> new String(Base64.getDecoder().decode(cookie.getValue())))
                .map(value -> {
                    try {
                        return objectMapper.readValue(value, UserInfo.class);
                    } catch (JsonProcessingException e) {
                        log.error("In the process of deserialization there was some problem. More details:", e);
                        throw new RuntimeException();
                    }
                })
                .map(userInfo -> Roles.valueOf(userInfo.role()));
    }

    private static boolean anyMatch(List<String> allowedCommands, String action) {
        return Stream.concat(statusCommands.stream(), allowedCommands.stream())
                .anyMatch(s -> s.equals(action));
    }
}
