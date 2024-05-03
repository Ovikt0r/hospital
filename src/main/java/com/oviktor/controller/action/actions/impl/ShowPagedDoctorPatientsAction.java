package com.oviktor.controller.action.actions.impl;

import com.oviktor.ApplicationContext;
import com.oviktor.controller.action.Actionable;
import com.oviktor.controller.action.actions.PagedPatientsAction;
import com.oviktor.dao.utils.Pagination;
import com.oviktor.dao.utils.Sorting;
import com.oviktor.dto.DoctorDto;
import com.oviktor.dto.PagedPatientsDto;
import com.oviktor.enums.PatientsSortBy;
import com.oviktor.service.MedicalStaffService;
import com.oviktor.service.PatientService;

public class ShowPagedDoctorPatientsAction extends PagedPatientsAction {

    private static final PatientService patientService = ApplicationContext.getInstance().getPatientService();
    private static final MedicalStaffService medicalStaffService = ApplicationContext.getInstance().getMedicalStaffService();

    public static final String DOCTOR_PATIENTS = "get_doctor_patients.jsp";




    public static Actionable getDoctorPatients() {
        return (request, response) -> {
            int pageNum = getPageNum(request);
            Sorting sorting = getSorting(request);
            PatientsSortBy sortBy = getSortBy(request);

            request.setAttribute("patientsSorting", sorting.toString());
            request.setAttribute("patientsSortBy", sortBy.toString());

            long doctorId = Long.parseLong(request.getParameter("doctorId"));

            PagedPatientsDto patients = switch (sortBy) {
                case NAME ->
                        patientService.getPatientsByDoctorSortedByName(doctorId, sorting, Pagination.pageNum(pageNum));
                case BIRTHDAY ->
                        patientService.getPatientsByDoctorSortedByDateOfBirth(doctorId, sorting, Pagination.pageNum(pageNum));
            };
            request.setAttribute("pagedPatients", patients);

            DoctorDto foundDoctor = medicalStaffService.getDoctorById(doctorId);
            request.setAttribute("doctor", foundDoctor);

            return excludePatient(DOCTOR_PATIENTS, request, response);
        };
    }
}
