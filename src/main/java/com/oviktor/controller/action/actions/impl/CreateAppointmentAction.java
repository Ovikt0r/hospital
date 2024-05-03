package com.oviktor.controller.action.actions.impl;

import com.oviktor.ApplicationContext;
import com.oviktor.controller.action.Actionable;
import com.oviktor.controller.action.actions.AbstractAction;
import com.oviktor.controller.action.actions.PagedDoctorsAction;
import com.oviktor.controller.action.actions.PagedPatientsAction;
import com.oviktor.dto.DoctorDto;
import com.oviktor.dto.MakeAppointmentDto;
import com.oviktor.dto.PatientDto;
import com.oviktor.enums.AppointmentPermissions;
import com.oviktor.enums.AppointmentTypes;
import com.oviktor.service.AppointmentService;
import com.oviktor.service.MedicalStaffService;
import com.oviktor.service.PatientService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
@Slf4j
public class CreateAppointmentAction extends AbstractAction {

    private static AppointmentService appointmentService = ApplicationContext.getInstance().getAppointmentService();
    private static MedicalStaffService medicalStaffService = ApplicationContext.getInstance().getMedicalStaffService();
    private static PatientService patientService = ApplicationContext.getInstance().getPatientService();

    CreateAppointmentAction(AppointmentService appointmentService, MedicalStaffService medicalStaffService,PatientService patientService) {
        CreateAppointmentAction.appointmentService = appointmentService;
        CreateAppointmentAction.medicalStaffService = medicalStaffService;
        CreateAppointmentAction.patientService = patientService;
        log.info("CreateAppointmentAction constructor is initialized");
    }
    public static Actionable createAppointment() {
        return (request, response) -> {
            appointmentService.makeAppointment(
                    getCurrentUserInfo(request),
                    MakeAppointmentDto.builder()
                            .patientId(Long.parseLong(request.getParameter("patientId")))
                            .doctorId(Long.parseLong(request.getParameter("doctorId")))
                            .appointmentType(AppointmentTypes.getById(Long.parseLong(request.getParameter("appointmentTypeId"))))
                            .appointmentDateTime(LocalDateTime.parse(request.getParameter("appointmentDateTime")))
                            .build()
            );
            log.info("The new appointment was created successfully");
            return successExcludingPatient(request, response);
        };
    }

    public static class CreateAppointmentForDoctorAction extends PagedPatientsAction {

        public static final String CREATE_APPOINTMENT_FOR_DOCTOR = "create_appointment_for_doctor.jsp";


        public static Actionable showCreateAppointmentForDoctorPage() {
            return (request, response) -> {
                long doctorId = Long.parseLong(request.getParameter("doctorId"));
                DoctorDto foundDoctor = medicalStaffService.getDoctorById(doctorId);
                request.setAttribute("doctor", foundDoctor);
                log.info("Setting the request attribute 'doctor'");

                if (request.getParameter("patientId") != null) {
                    long patientId = Long.parseLong(request.getParameter("patientId"));
                    PatientDto foundPatient = patientService.getPatientById(patientId);
                    request.setAttribute("patient", foundPatient);
                    log.info("Setting the request attribute 'patient'");
                }

                return getRole(request)
                        .map(role -> {
                            request.setAttribute("appointmentTypes", AppointmentPermissions.getByRole(role));
                            return switch (role) {
                                case PATIENT -> forbidden(request, response);
                                case DOCTOR -> DOCTOR_PREFIX + CREATE_APPOINTMENT_FOR_DOCTOR;
                                case NURSE -> NURSE_PREFIX + CREATE_APPOINTMENT_FOR_DOCTOR;
                                case ADMIN -> ADMIN_PREFIX + CREATE_APPOINTMENT_FOR_DOCTOR;
                            };
                        })
                        .orElseGet(() -> internalServerError(request, response));
            };
        }
    }

    public static class CreateAppointmentForPatientAction extends PagedDoctorsAction {

        private static final String CREATE_APPOINTMENT_FOR_PATIENT = "create_appointment_for_patient.jsp";


        public static Actionable showCreateAppointmentForPatientPage() {
            return (request, response) -> {
                long patientId = Long.parseLong(request.getParameter("patientId"));
                PatientDto foundPatient = patientService.getPatientById(patientId);
                request.setAttribute("patient", foundPatient);
                log.info("Setting the request attribute 'patient'");

                return getRole(request)
                        .map(role -> {
                            request.setAttribute("appointmentTypes", AppointmentPermissions.getByRole(role));
                            return switch (role) {
                                case PATIENT -> forbidden(request, response);
                                case DOCTOR -> {
                                    request.setAttribute("doctor", medicalStaffService.getDoctorById(getCurrentUserInfo(request).id()));
                                    log.info("Setting attribute 'doctor' and redirect to the page of appointment creation");
                                    yield DOCTOR_PREFIX + CREATE_APPOINTMENT_FOR_PATIENT;
                                }
                                case NURSE -> {
                                    if (request.getParameter("doctorId") != null) {
                                        long doctorId = Long.parseLong(request.getParameter("doctorId"));
                                        DoctorDto doctor = medicalStaffService.getDoctorById(doctorId);
                                        request.setAttribute("doctor", doctor);
                                        log.info("Setting attribute 'doctor' and redirect to the page of appointment creation");
                                    }
                                    yield NURSE_PREFIX + CREATE_APPOINTMENT_FOR_PATIENT;
                                }
                                case ADMIN -> {
                                    if (request.getParameter("doctorId") != null) {
                                        long doctorId = Long.parseLong(request.getParameter("doctorId"));
                                        DoctorDto doctor = medicalStaffService.getDoctorById(doctorId);
                                        request.setAttribute("doctor", doctor);
                                        log.info("Setting attribute 'doctor' and redirect to the page of appointment creation");
                                    }
                                    yield ADMIN_PREFIX + CREATE_APPOINTMENT_FOR_PATIENT;
                                }
                            };
                        }).orElseGet(() -> internalServerError(request, response));
            };
        }
    }
}
