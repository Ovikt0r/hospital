package com.oviktor.controller.action.actions.impl;

import com.oviktor.controller.action.Actionable;
import com.oviktor.controller.action.actions.AbstractAction;

public class StatusAction extends AbstractAction {

    public static Actionable showSuccessPage() {
        return (request, response) -> SUCCESS_PAGE;
    }

    public static Actionable showErrorPage() {
        return (request, response) -> ERROR_PAGE;
    }

    public static Actionable showForbiddenPage() {
        return (request, response) -> FORBIDDEN_PAGE;
    }

    public static Actionable showNotFoundPage() {
        return (request, response) -> NOT_FOUND_PAGE;
    }
}
