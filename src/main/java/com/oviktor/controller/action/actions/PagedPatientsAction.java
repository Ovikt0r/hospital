package com.oviktor.controller.action.actions;

import com.oviktor.ApplicationContext;
import com.oviktor.controller.action.Actionable;
import com.oviktor.dao.utils.Pagination;
import com.oviktor.dao.utils.Sorting;
import com.oviktor.dto.PagedPatientsDto;
import com.oviktor.enums.PatientsSortBy;
import com.oviktor.service.PatientService;
import jakarta.servlet.http.HttpServletRequest;

public abstract class PagedPatientsAction extends PagedAction {

    protected static PatientService patientService = ApplicationContext.getInstance().getPatientService();

    private static final String CHOOSE_PATIENT_FOR_APPOINTMENT_CREATION_PAGE = "choose_patient_for_appointment_creation.jsp";

    protected static PatientsSortBy getSortBy(HttpServletRequest req) {
        return req.getParameter("sortBy") != null  && !req.getParameter("sortBy").isEmpty()
                ? PatientsSortBy.valueOf(req.getParameter("sortBy").toUpperCase()) : PatientsSortBy.NAME;
    }

    public static Actionable showChoosePatientForAppointmentCreationPage() {
        return showChoosePatientPage(CHOOSE_PATIENT_FOR_APPOINTMENT_CREATION_PAGE);
    }

    private static Actionable showChoosePatientPage(String path) {
        return (request, response) -> {
            setUpPagedPatients(request);
            return excludePatient(path, request, response);
        };
    }

    protected static void setUpPagedPatients(HttpServletRequest request) {
        int pageNum = getPageNum(request);
        Sorting sorting = getSorting(request);
        PatientsSortBy sortBy = getSortBy(request);

        request.setAttribute("patientsSorting", sorting.toString());
        request.setAttribute("patientsSortBy", sortBy.toString());

        PagedPatientsDto patients = switch (sortBy) {
            case NAME -> patientService.getPatientsSortedByName(sorting, Pagination.pageNum(pageNum));
            case BIRTHDAY -> patientService.getPatientsSortedByDateOfBirth(sorting, Pagination.pageNum(pageNum));
        };
        request.setAttribute("pagedPatients", patients);
    }
}
