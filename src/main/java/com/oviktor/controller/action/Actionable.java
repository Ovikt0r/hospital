package com.oviktor.controller.action;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 *
 */

@FunctionalInterface
public interface Actionable {

    /**
     * Gets the page address from the request and response arguments
     * @param request an {@link HttpServletRequest} object that contains the request the client has made of the servlet
     *
     * @param response an {@link HttpServletResponse} object that contains the response the servlet sends to the client
     *
     * @return URI or URL in string form of the page after working on the client request/response
     */
    String execute(HttpServletRequest request, HttpServletResponse response);
}
