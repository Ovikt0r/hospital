package com.oviktor.controller.action.actions;

import com.oviktor.dao.utils.Sorting;
import jakarta.servlet.http.HttpServletRequest;

public abstract class PagedAction extends AbstractAction {

    protected static int getPageNum(HttpServletRequest request) {
        return request.getParameter("pageNum") != null  && !request.getParameter("pageNum").isEmpty()
                ? Integer.parseInt(request.getParameter("pageNum")) : 1;
    }

    protected static Sorting getSorting(HttpServletRequest request) {
        return request.getParameter("sorting") != null && !request.getParameter("sorting").isEmpty()
                ? Sorting.valueOf(request.getParameter("sorting").toUpperCase()) : Sorting.ASC;
    }
}
