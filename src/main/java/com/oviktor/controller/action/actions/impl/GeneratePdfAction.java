package com.oviktor.controller.action.actions.impl;

import com.oviktor.ApplicationContext;
import com.oviktor.controller.action.Actionable;
import com.oviktor.controller.action.actions.AbstractAction;
import com.oviktor.dto.PatientDto;
import com.oviktor.dto.UserInfo;
import com.oviktor.entity.Appointment;
import com.oviktor.service.AppointmentService;
import com.oviktor.service.DischargePdfService;
import com.oviktor.service.MedicalStaffService;
import com.oviktor.service.PatientService;

public class GeneratePdfAction extends AbstractAction {

    public static final PatientService patientService = ApplicationContext.getInstance().getPatientService();
    public static final MedicalStaffService medicalStaffService = ApplicationContext.getInstance().getMedicalStaffService();
    public static final AppointmentService appointmentService = ApplicationContext.getInstance().getAppointmentService();
    public static final DischargePdfService dischargePdfService =
            ApplicationContext.getInstance().getDischargePdfService();

    public static Actionable createPdfDischargeForm() {
        return ((request, response) -> {
            long patientId = Long.parseLong(request.getParameter("patientId"));
            PatientDto patient = patientService.getPatientById(patientId);
            request.setAttribute("patient", patient);
            long appointmentId = Long.parseLong(request.getParameter("appointmentId"));
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            request.setAttribute("appointment", appointment);
            UserInfo currentUserInfo = getCurrentUserInfo(request);
            String language = request.getSession().getAttribute("language").toString();
            return getRole(request)
                    .map(roles -> switch (roles) {
                        case PATIENT, NURSE -> forbidden(request, response);
                        case DOCTOR, ADMIN -> {
                            dischargePdfService.generatePdf(response, language, currentUserInfo, patientId, appointmentId);
                            yield SUCCESS_PAGE;
                        }
                    }).orElseGet(() -> internalServerError(request, response));
        });
    }
}
