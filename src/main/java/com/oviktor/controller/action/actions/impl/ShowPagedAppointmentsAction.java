package com.oviktor.controller.action.actions.impl;

import com.oviktor.ApplicationContext;
import com.oviktor.controller.action.Actionable;
import com.oviktor.controller.action.actions.PagedAction;
import com.oviktor.dao.utils.Pagination;
import com.oviktor.dao.utils.Sorting;
import com.oviktor.dto.AppointmentDto;
import com.oviktor.dto.PagedAppointmentsDto;
import com.oviktor.service.AppointmentService;

public class ShowPagedAppointmentsAction extends PagedAction {

    private static final AppointmentService appointmentService = ApplicationContext.getInstance().getAppointmentService();

    private static final String PAGED_APPOINTMENTS_PAGE = "paged_appointments_page.jsp";
    private static final String APPOINTMENT_CARD_PAGE = "appointment_card.jsp";


    public static Actionable showDoctorAppointmentsPage() {
        return (request, response) -> {
            int pageNum = getPageNum(request);
            Sorting sorting = getSorting(request);

            request.setAttribute("appointmentsSorting", sorting.toString());

            long doctorId = Long.parseLong(request.getParameter("doctorId"));
            request.setAttribute("dataToSave", "&doctorId=" + doctorId);

            PagedAppointmentsDto foundAppointments = appointmentService.getAppointmentsByDoctorId(doctorId, sorting, Pagination.pageNum(pageNum));

            request.setAttribute("pagedAppointments", foundAppointments);

            return excludePatient(PAGED_APPOINTMENTS_PAGE, request, response);
        };
    }

    public static Actionable showPatientAppointmentsPage() {
        return (request, response) -> {
            int pageNum = getPageNum(request);
            Sorting sorting = getSorting(request);

            request.setAttribute("appointmentsSorting", sorting.toString());

            long patientId = Long.parseLong(request.getParameter("patientId"));
            request.setAttribute("dataToSave", "&patientId=" + patientId);

            PagedAppointmentsDto foundAppointments = appointmentService.getAppointmentsByPatientId(patientId, sorting, Pagination.pageNum(pageNum));

            request.setAttribute("pagedAppointments", foundAppointments);

            return excludeNone(PAGED_APPOINTMENTS_PAGE, request, response);
        };
    }

    public static Actionable showAppointmentCardPage() {
        return (request, response) -> {
            long appointmentId = Long.parseLong(request.getParameter("appointmentId"));

            AppointmentDto appointment = appointmentService.getAppointmentWithDiagnosisById(appointmentId);

            request.setAttribute("appointment", appointment);

            return excludeNone(APPOINTMENT_CARD_PAGE, request, response);
        };
    }
}
