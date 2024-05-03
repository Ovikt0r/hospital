package com.oviktor.controller.action.actions.impl;

import com.oviktor.ApplicationContext;
import com.oviktor.controller.action.Actionable;
import com.oviktor.controller.action.actions.PagedAction;
import com.oviktor.dao.utils.Pagination;
import com.oviktor.dao.utils.Sorting;
import com.oviktor.dto.PagedNursesDto;
import com.oviktor.service.MedicalStaffService;

public class ShowPagedNursesAction extends PagedAction {

    private static final MedicalStaffService medicalStaffService = ApplicationContext.getInstance().getMedicalStaffService();

    public static final String NURSES_PAGE = "get_all_nurses.jsp";


    public static Actionable showPagedNurses() {
        return (request, response) -> {
            int pageNum = getPageNum(request);
            Sorting sorting = getSorting(request);

            PagedNursesDto nurses = medicalStaffService.getNursesSortedByName(sorting, Pagination.pageNum(pageNum));
            request.setAttribute("pagedNurses", nurses);

            return excludePatientAndNurse(NURSES_PAGE, request, response);
        };
    }
}
