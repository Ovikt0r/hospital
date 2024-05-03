package com.oviktor.controller.action.actions.impl;

import com.oviktor.ApplicationContext;
import com.oviktor.controller.action.Actionable;
import com.oviktor.controller.action.actions.PagedDoctorsAction;
import com.oviktor.dto.CreateUserDto;
import com.oviktor.dto.DoctorDto;
import com.oviktor.dto.UserInfo;
import com.oviktor.service.MedicalStaffService;
import com.oviktor.service.PatientService;
import com.password4j.Password;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;

public class CreatePatientAction extends PagedDoctorsAction {

    private static final MedicalStaffService medicalStaffService = ApplicationContext.getInstance().getMedicalStaffService();
    private static final PatientService patientService = ApplicationContext.getInstance().getPatientService();

    private static final String CREATE_PATIENT_PAGE = "create_patient.jsp";


    public static Actionable showCreatePatientPage() {
        return (request, response) -> getRole(request)
                .map(roles -> switch (roles) {
                    case ADMIN -> {
                        setUpForAdminAndNurse(request);
                        yield ADMIN_PREFIX + CREATE_PATIENT_PAGE;
                    }
                    case NURSE -> {
                        setUpForAdminAndNurse(request);
                        yield NURSE_PREFIX + CREATE_PATIENT_PAGE;
                    }
                    case DOCTOR -> {
                        setUpForDoctor(request);
                        yield DOCTOR_PREFIX + CREATE_PATIENT_PAGE;
                    }
                    case PATIENT -> forbidden(request, response);
                }).orElseGet(() -> internalServerError(request, response));
    }

    private static void setUpForAdminAndNurse(HttpServletRequest request) {
        if (request.getParameter("doctorId") != null) {
            long doctorId = Long.parseLong(request.getParameter("doctorId"));
            DoctorDto foundDoctor = medicalStaffService.getDoctorById(doctorId);
            request.setAttribute("doctor", foundDoctor);
        }
    }

    private static void setUpForDoctor(HttpServletRequest request) {
        UserInfo currentUserInfo = getCurrentUserInfo(request);
        DoctorDto foundDoctor = medicalStaffService.getDoctorById(currentUserInfo.id());
        request.setAttribute("doctor", foundDoctor);
    }

    public static Actionable createPatient() {
        return (request, response) -> {
            patientService.createPatient(
                    CreateUserDto.builder()
                            .firstName(request.getParameter("firstName"))
                            .lastName(request.getParameter("lastName"))
                            .phone(request.getParameter("phone"))
                            .email(request.getParameter("email"))
                            .usersPassword(Password.hash(request.getParameter("password")).withBcrypt().getResult())
                            .dateOfBirth(LocalDate.parse(request.getParameter("birthday")))
                            .build(),
                    Long.parseLong(request.getParameter("doctorId"))
            );
            return successExcludingPatient(request, response);
        };
    }
}
