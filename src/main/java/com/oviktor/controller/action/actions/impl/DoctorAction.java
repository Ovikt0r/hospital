package com.oviktor.controller.action.actions.impl;

import com.oviktor.ApplicationContext;
import com.oviktor.controller.action.Actionable;
import com.oviktor.controller.action.actions.AbstractAction;
import com.oviktor.dto.CreateUserDto;
import com.oviktor.dto.DoctorDto;
import com.oviktor.dto.UserInfo;
import com.oviktor.enums.MedicineCategories;
import com.oviktor.enums.Roles;
import com.oviktor.service.MedicalStaffService;
import com.password4j.Password;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;


@Slf4j
public class DoctorAction extends AbstractAction {

    private static MedicalStaffService medicalStaffService = ApplicationContext.getInstance().getMedicalStaffService();


    DoctorAction(MedicalStaffService medicalStaffService){
        DoctorAction.medicalStaffService = medicalStaffService;
    }
    private static final String SHOW_DOCTOR_BY_ID = "doctor_card.jsp";
    private static final String CREATE_DOCTOR = "create_doctor.jsp";


    public static Actionable createDoctor() {
        return (request, response) -> getRole(request)
                .map(roles -> switch (roles) {
                    case ADMIN -> {
                        medicalStaffService.createDoctor(
                                CreateUserDto.builder()
                                        .firstName(request.getParameter("firstName"))
                                        .lastName(request.getParameter("lastName"))
                                        .phone(request.getParameter("phone"))
                                        .email(request.getParameter("email"))
                                        .usersPassword(Password.hash(request.getParameter("password")).withBcrypt().getResult())
                                        .dateOfBirth(LocalDate.parse(request.getParameter("birthday")))
                                        .build(),
                                MedicineCategories.getById(Long.parseLong(request.getParameter("categoryId")))
                        );
                        log.info("The user with the doctoral role was successfully created");
                        yield success(request, response);
                    }
                    case DOCTOR, PATIENT, NURSE -> forbidden(request, response);
                }).orElseGet(() -> internalServerError(request, response));
    }

    public static Actionable showCreateDoctorPage() {
        return (request, response) -> {
            request.setAttribute("categories", MedicineCategories.getAll());

            return includeAdmin(CREATE_DOCTOR, request, response);
        };
    }

    public static Actionable showDoctorCardPage() {
        return (request, response) -> {
            long doctorIdToFind = Long.parseLong(request.getParameter("doctorId"));
            UserInfo currentUserInfo = getCurrentUserInfo(request);
            return switch (Roles.valueOf(currentUserInfo.role())) {
                case ADMIN -> {
                    setUpDoctor(request, doctorIdToFind);
                    yield ADMIN_PREFIX + SHOW_DOCTOR_BY_ID;
                }
                case DOCTOR -> {
                    if (doctorIdToFind == currentUserInfo.id()) {
                        setUpDoctor(request, doctorIdToFind);
                        yield DOCTOR_PREFIX + SHOW_DOCTOR_BY_ID;
                    }
                    yield forbidden(request, response);
                }
                case NURSE -> {
                    setUpDoctor(request, doctorIdToFind);
                    yield NURSE_PREFIX + SHOW_DOCTOR_BY_ID;
                }
                case PATIENT -> {
                    setUpDoctor(request, doctorIdToFind);
                    yield PATIENT_PREFIX + SHOW_DOCTOR_BY_ID;
                }
            };
        };
    }

    private static void setUpDoctor(HttpServletRequest request, long doctorIdToFind) {
        DoctorDto foundDoctor = medicalStaffService.getDoctorById(doctorIdToFind);
        request.setAttribute("foundDoctor", foundDoctor);
    }
}
