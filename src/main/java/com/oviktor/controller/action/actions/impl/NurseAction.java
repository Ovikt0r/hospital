package com.oviktor.controller.action.actions.impl;

import com.oviktor.ApplicationContext;
import com.oviktor.controller.action.Actionable;
import com.oviktor.controller.action.actions.AbstractAction;
import com.oviktor.dto.CreateUserDto;
import com.oviktor.dto.NurseDto;
import com.oviktor.enums.MedicineCategories;
import com.oviktor.service.MedicalStaffService;
import com.password4j.Password;

import java.time.LocalDate;

public class NurseAction extends AbstractAction {

    private static MedicalStaffService medicalStaffService = ApplicationContext.getInstance().getMedicalStaffService();
    NurseAction(MedicalStaffService medicalStaffService){
        NurseAction.medicalStaffService = medicalStaffService;
    }
    public static final String NURSE_CARD = "nurse_card.jsp";
    private static final String CREATE_NURSE = "create_nurse.jsp";


    public static Actionable showNurseCard() {
        return (request, response) -> {
            long nurseId = Long.parseLong(request.getParameter("nurseId"));

            NurseDto foundNurse = medicalStaffService.getNurseById(nurseId);
            request.setAttribute("foundNurse", foundNurse);

            return excludePatient(NURSE_CARD, request, response);
        };
    }

    public static Actionable createNurse() {
        return (request, response) -> getRole(request)
                .map(roles -> switch (roles) {
                    case ADMIN -> {
                        medicalStaffService.createNurse(
                                CreateUserDto.builder()
                                        .firstName(request.getParameter("firstName"))
                                        .lastName(request.getParameter("lastName"))
                                        .phone(request.getParameter("phone"))
                                        .email(request.getParameter("email"))
                                        .usersPassword(Password.hash(request.getParameter("password")).withBcrypt().getResult())
                                        .dateOfBirth(LocalDate.parse(request.getParameter("birthday")))
                                        .build()
                        );
                        yield success(request, response);
                    }
                    case DOCTOR, PATIENT, NURSE -> forbidden(request, response);
                }).orElseGet(() -> internalServerError(request, response));
    }

    public static Actionable showCreateNursePage() {
        return (request, response) -> {
            request.setAttribute("categories", MedicineCategories.getAll());
            return includeAdmin(CREATE_NURSE, request, response);
        };
    }
}
