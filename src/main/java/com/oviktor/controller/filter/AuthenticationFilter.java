package com.oviktor.controller.filter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oviktor.ApplicationContext;
import com.oviktor.controller.action.Commands;
import com.oviktor.dto.UserInfo;
import com.oviktor.dto.UserNameDto;
import com.oviktor.service.SecurityService;
import com.oviktor.utils.CookiesReader;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "AuthenticationFilter")
public class AuthenticationFilter implements Filter {

    private final SecurityService securityService = ApplicationContext.getInstance().getSecurityService();
    private final ObjectMapper objectMapper = ApplicationContext.getInstance().getObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (httpRequest.getParameter("action") != null &&
                (httpRequest.getParameter("action").equals(Commands.LOGIN) ||
                        httpRequest.getParameter("action").equals(Commands.LOGIN_PAGE))) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<UserInfo> userInfoOpt = CookiesReader.readCookieValue("user-info", httpRequest)
                .map((value) -> {
                    String valueStr = new String(Base64.getDecoder().decode(value));
                    try {
                        return objectMapper.readValue(valueStr, UserInfo.class);
                    } catch (JsonProcessingException e) {
                        log.error("In the process of deserialization there was some problem. More details:", e);
                        throw new RuntimeException(e);
                    }
                });

        if (userInfoOpt.isEmpty()
                || userInfoOpt.get().id() == null
                || userInfoOpt.get().role() == null
                || userInfoOpt.get().role().isEmpty()) {
            httpResponse.sendRedirect("controller?action=loginPage");
            return;
        }

        HttpSession session = httpRequest.getSession();
        if (session.getAttribute("currentUserLastName") == null ||
                session.getAttribute("currentUserFirstName") == null) {
            UserNameDto userNameDto = securityService.getUserNameById(userInfoOpt.get().id());
            session.setAttribute("currentUserLastName", userNameDto.lastName());
            session.setAttribute("currentUserFirstName", userNameDto.firstName());
        }
        filterChain.doFilter(request, response);
    }
}
