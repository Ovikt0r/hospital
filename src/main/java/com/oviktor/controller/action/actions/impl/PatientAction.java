package com.oviktor.controller.action.actions.impl;

import com.oviktor.ApplicationContext;
import com.oviktor.controller.action.Actionable;
import com.oviktor.controller.action.actions.AbstractAction;
import com.oviktor.dto.DoctorCompactedDto;
import com.oviktor.dto.PatientDto;
import com.oviktor.service.AppointmentService;
import com.oviktor.service.MedicalStaffService;
import com.oviktor.service.PatientService;

import java.util.List;

public class PatientAction extends AbstractAction {

    private static final PatientService patientService = ApplicationContext.getInstance().getPatientService();
    private static final MedicalStaffService medicalStaffService = ApplicationContext.getInstance().getMedicalStaffService();
    protected static AppointmentService appointmentService = ApplicationContext.getInstance().getAppointmentService();
    public static final String PATIENT_CARD = "patient_card.jsp";
    public static final String DISCHARGE_PAGE = "discharge_page.jsp";


    public static Actionable showPatientCard() {
        return (request, response) -> {
            long patientId = Long.parseLong(request.getParameter("patientId"));

            PatientDto foundPatient = patientService.getPatientById(patientId);
            request.setAttribute("foundPatient", foundPatient);
            if(!foundPatient.getIsTreated()) {
                long lastAppointmentId = appointmentService.getLastAppointmentByTreatedPatientId(patientId).getId();
                request.setAttribute("lastAppointmentId",lastAppointmentId);
            }
            return getRole(request)
                    .map(roles -> switch (roles) {
                        case ADMIN, NURSE -> {
                            request.setAttribute("mayBeDischarged", foundPatient.getIsTreated());
                            yield ADMIN_PREFIX + PATIENT_CARD;
                        }
                        case DOCTOR -> {
                            boolean isTreatedByDoctor = patientService.checkPatientIsTreatedByDoctor(patientId, getCurrentUserInfo(request).id());
                            boolean mayBeDischarged = isTreatedByDoctor && foundPatient.getIsTreated();
                            request.setAttribute("mayBeDischarged", mayBeDischarged);
                            request.setAttribute("doctorId", getCurrentUserInfo(request).id());
                            yield DOCTOR_PREFIX + PATIENT_CARD;
                        }
                        case PATIENT -> {
                            request.setAttribute("mayBeDischarged", false);
                            yield forbidden(request, response);
                        }
                    }).orElseGet(() -> internalServerError(request, response));
        };
    }

    public static Actionable dischargePatientPage() {
        return (request, response) -> {
            long patientId = Long.parseLong(request.getParameter("patientId"));
            PatientDto patient = patientService.getPatientById(patientId);

            request.setAttribute("patient", patient);

            List<DoctorCompactedDto> doctors = medicalStaffService.getDoctorsByPatientId(patientId);
            request.setAttribute("doctors", doctors);

            return excludePatientAndNurse(DISCHARGE_PAGE, request, response);
        };
    }

    public static Actionable dischargePatient() {
        return (request, response) -> {
            long patientId = Long.parseLong(request.getParameter("patientId"));
            long doctorId = Long.parseLong(request.getParameter("doctorId"));
            String diagnosisText = request.getParameter("text");

            return getRole(request)
                    .map(roles -> switch (roles) {
                        case ADMIN, DOCTOR -> {
                            patientService.dischargePatient(patientId, doctorId, diagnosisText);
                            yield success(request, response);
                        }
                        case PATIENT, NURSE -> forbidden(request, response);
                    }).orElseGet(() -> internalServerError(request, response));
        };
    }
}
