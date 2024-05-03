package com.oviktor.controller.filter;

import com.oviktor.controller.action.Commands;
import com.oviktor.enums.Roles;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;
import java.util.List;

public class AuthorizationFilters {

    @WebFilter(urlPatterns = {"/*"}, filterName = "AdminAuthorizationFilter")
    public static class AdminFilter extends GenericAuthorizationFilter {

        private final static List<String> allowedCommands = Commands.getAdminCommands();

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
                throws IOException, ServletException {
            super.proceedRequest(allowedCommands, Roles.ADMIN, request, response, filterChain);
        }
    }

    @WebFilter(urlPatterns = {"/*"}, filterName = "DoctorAuthorizationFilter")
    public static class DoctorFilter extends GenericAuthorizationFilter {

        private final static List<String> allowedCommands = Commands.getDoctorCommands();

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
                throws IOException, ServletException {
            super.proceedRequest(allowedCommands, Roles.DOCTOR, request, response, filterChain);
        }
    }

    @WebFilter(urlPatterns = {"/*"}, filterName = "NurseAuthorizationFilter")
    public static class NurseFilter extends GenericAuthorizationFilter {

        private final static List<String> allowedCommands = Commands.getNurseCommands();

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
                throws IOException, ServletException {
            super.proceedRequest(allowedCommands, Roles.NURSE, request, response, filterChain);
        }
    }

    @WebFilter(urlPatterns = {"/*"}, filterName = "PatientAuthorizationFilter")
    public static class PatientFilter extends GenericAuthorizationFilter {

        private final static List<String> allowedCommands = Commands.getPatientCommands();

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
                throws IOException, ServletException {
            super.proceedRequest(allowedCommands, Roles.PATIENT, request, response, filterChain);
        }
    }
}
