package com.oviktor.controller.action.actions.impl;

import com.oviktor.controller.action.Actionable;
import com.oviktor.controller.action.actions.PagedPatientsAction;
import com.oviktor.service.PatientService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShowPagedPatientsAction extends PagedPatientsAction {

    private static final String PATIENTS_PAGE = "get_all_patients.jsp";
    ShowPagedPatientsAction(PatientService patientService) {
        PagedPatientsAction.patientService = patientService;
    }

    public static Actionable showPagedPatientsPage() {
        return (request, response) -> {
            log.info("Start setting up paged patients");
            setUpPagedPatients(request);
            log.info("Setting up paged patients was successfully");
            return excludePatient(PATIENTS_PAGE, request, response);
        };
    }
}
