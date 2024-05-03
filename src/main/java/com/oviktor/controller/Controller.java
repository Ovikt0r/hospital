package com.oviktor.controller;

import com.oviktor.controller.action.ActionFactory;
import com.oviktor.controller.action.Actionable;
import com.oviktor.controller.action.Commands;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Controller class a single servlet that handles all requests and implements the business logic of the application
 * is called the Front Controller or Controller Servlet. It is typically the central point of an application
 * and coordinates the processing of requests and responses between various components of the application
 * such as filters, models, views, etc.
 */
@Slf4j
@WebServlet(value = "/controller", name = "Front Controller")
public class Controller extends HttpServlet {

    private final ActionFactory actionFactory = ActionFactory.actionFactory();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("Do method started to work");
        processRequest(req, resp);
        log.debug("Do method's finished working");
     }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("Post method started to work");
        processRequest(req, resp);
        log.debug("Post method's finished working");
    }

    /**
     *  {@link Controller#processRequest(HttpServletRequest, HttpServletResponse)} is a method used in Java Servlets to process an incoming HTTP
     *  request and generate an HTTP response. This method is typically called by the doGet(), doPost(), doPut(), doDelete(),
     *  and other HTTP method-specific methods in the servlet.
     * @param req an {@link HttpServletRequest} object contains information about the incoming request, such as the HTTP method, headers and parameters,while
     * @param resp an {@link HttpServletResponse} object is used to create and send the HTTP response back to the client.
     *
     * Within the processRequest method, the servlet developer can implement the business logic of the application and use
     * the HttpServletRequest object to access and manipulate the data sent by the client, and use
     * the HttpServletResponse object to generate a response to be sent back to the client.
     * @throws ServletException
     * @throws IOException
     */

    private void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Actionable action = actionFactory.getAction(req);
        log.info("Current triggered action is {}", action.toString());
        String page = action.execute(req, resp);
        log.info("URI of the taken page is {}", page);
        RequestDispatcher dispatcher = req.getRequestDispatcher(page);
        if (!page.equals(Commands.StatusCommands.REDIRECT)) {
            log.debug("The taken page is {}, current request and response will be forward",page);
            dispatcher.forward(req, resp);
            log.debug("Redirection was successful");
        }
    }
}
