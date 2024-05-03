package com.oviktor.controller.action.actions;

import com.oviktor.ApplicationContext;
import com.oviktor.controller.action.Actionable;
import com.oviktor.dao.utils.Pagination;
import com.oviktor.dao.utils.Sorting;
import com.oviktor.dto.PagedDoctorsDto;
import com.oviktor.enums.DoctorsSortBy;
import com.oviktor.enums.MedicineCategories;
import com.oviktor.service.MedicalStaffService;
import jakarta.servlet.http.HttpServletRequest;

public abstract class PagedDoctorsAction extends PagedAction {

    protected static MedicalStaffService medicalStaffService = ApplicationContext.getInstance().getMedicalStaffService();

    private static final String CHOOSE_DOCTOR_FOR_PATIENT_CREATION_PAGE = "choose_doctor_for_patient_creation.jsp";
    private static final String CHOOSE_DOCTOR_FOR_APPOINTMENT_CREATION_PAGE = "choose_doctor_for_appointment_creation.jsp";

    protected static DoctorsSortBy getSortBy(HttpServletRequest req) {
        return req.getParameter("sortBy") != null  && !req.getParameter("sortBy").isEmpty()
                ? DoctorsSortBy.valueOf(req.getParameter("sortBy").toUpperCase()) : DoctorsSortBy.NAME;
    }

    protected static MedicineCategories getFilterByCategory(HttpServletRequest request) {
        return request.getParameter("categoryId") != null  && !request.getParameter("categoryId").isEmpty()
                ? MedicineCategories.getById(Long.parseLong(request.getParameter("categoryId"))) : null;
    }

    public static Actionable showChooseDoctorForPatientCreationPage() {
        return showChooseDoctorPage(CHOOSE_DOCTOR_FOR_PATIENT_CREATION_PAGE);
    }

    public static Actionable showChooseDoctorForAppointmentCreationPage() {
        return showChooseDoctorPage(CHOOSE_DOCTOR_FOR_APPOINTMENT_CREATION_PAGE);
    }

    private static Actionable showChooseDoctorPage(String path) {
        return (request, response) -> {
            setUpPagedDoctors(request);
            return excludePatientAndDoctor(path, request, response);
        };
    }

    protected static void setUpPagedDoctors(HttpServletRequest request) {
        int pageNum = getPageNum(request);
        Sorting sorting = getSorting(request);
        DoctorsSortBy sortBy = getSortBy(request);
        MedicineCategories filterByCategory = getFilterByCategory(request);

        request.setAttribute("sorting", sorting.toString());
        request.setAttribute("sortBy", sortBy.toString());
        request.setAttribute("categories", MedicineCategories.getAll());
        request.setAttribute("categoryId", filterByCategory);

        PagedDoctorsDto doctors = switch (sortBy) {
            case NAME ->
                    medicalStaffService.getDoctorsSortedByName(sorting, filterByCategory, Pagination.pageNum(pageNum));
            case NUM ->
                    medicalStaffService.getDoctorsByNumberOfPatients(sorting, filterByCategory, Pagination.pageNum(pageNum));
        };
        request.setAttribute("pagedDoctors", doctors);
    }
}
