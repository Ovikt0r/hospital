package com.oviktor.controller.action.actions.impl;

import com.oviktor.controller.action.Actionable;
import com.oviktor.controller.action.actions.PagedDoctorsAction;
import com.oviktor.service.MedicalStaffService;

public class ShowPagedDoctorsAction extends PagedDoctorsAction {

    private static final String PAGED_DOCTORS_PAGE = "get_all_doctors.jsp";

    ShowPagedDoctorsAction(MedicalStaffService medicalStaffService) {
        PagedDoctorsAction.medicalStaffService = medicalStaffService;
    }
    public static Actionable showPagedDoctorsPage() {
        return (request, response) -> {
            setUpPagedDoctors(request);

            return includeAdminAndNurse(PAGED_DOCTORS_PAGE, request, response);
        };
    }
}
