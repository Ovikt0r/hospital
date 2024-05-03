package com.oviktor.controller.action.actions.impl;

import com.oviktor.ApplicationContext;
import com.oviktor.controller.action.Actionable;
import com.oviktor.controller.action.actions.PagedAction;
import com.oviktor.dto.TypesDto;
import com.oviktor.dto.UserInfo;
import com.oviktor.entity.Diagnosis;
import com.oviktor.enums.DiagnosisTypes;
import com.oviktor.service.DiagnosisService;

import java.util.List;

public class DiagnosisAction extends PagedAction {

    private static DiagnosisService diagnosisService = ApplicationContext.getInstance().getDiagnosisService();

    private static final String SET_DIAGNOSIS_PAGE = "set_diagnosis_page.jsp";
    private static final String UPDATE_DIAGNOSIS_PAGE = "update_diagnosis_page.jsp";

    DiagnosisAction(DiagnosisService diagnosisService) {
        DiagnosisAction.diagnosisService = diagnosisService;
    }

    public static Actionable showSetDiagnosisPage() {
        return (request, response) -> {
            long appointmentId = Long.parseLong(request.getParameter("appointmentId"));
            request.setAttribute("appointmentId", appointmentId);
            request.setAttribute("diagnosisOptions", DiagnosisTypes.getAll());

            return excludePatientAndNurse(SET_DIAGNOSIS_PAGE, request, response);
        };
    }

    public static Actionable setDiagnosis() {
        return (request, response) -> {
            String text = request.getParameter("text");
            DiagnosisTypes diagnosisType = DiagnosisTypes.valueOf(request.getParameter("diagnosisType"));
            long appointmentId = Long.parseLong(request.getParameter("appointmentId"));

            UserInfo currentUserInfo = getCurrentUserInfo(request);
            diagnosisService.diagnose(currentUserInfo, appointmentId, text, diagnosisType);

            return successExcludingPatientAndNurse(request, response);
        };
    }

    public static Actionable showUpdateDiagnosisPage() {
        return (request, response) -> {
            long diagnosisId = Long.parseLong(request.getParameter("diagnosisId"));

            Diagnosis currentDiagnosis = diagnosisService.getDiagnosisById(diagnosisId);
            request.setAttribute("currentDiagnosis", currentDiagnosis);

            TypesDto currentDiagnosisType = new TypesDto(
                    currentDiagnosis.getDiagnosisType().getId(),
                    currentDiagnosis.getDiagnosisType().toString()
            );
            request.setAttribute("selectedType", currentDiagnosisType);

            List<TypesDto> diagnosisOptions = DiagnosisTypes.getAll();
            diagnosisOptions.remove(currentDiagnosisType);
            request.setAttribute("diagnosisOptions", diagnosisOptions);

            return excludePatientAndNurse(UPDATE_DIAGNOSIS_PAGE, request, response);
        };
    }

    public static Actionable updateDiagnosis() {
        return (request, response) -> {
            long diagnosisId = Long.parseLong(request.getParameter("diagnosisId"));
            String text = request.getParameter("text");
            DiagnosisTypes diagnosisType = DiagnosisTypes.getById(Long.parseLong(request.getParameter("diagnosisType")));

            diagnosisService.updateDiagnosis(diagnosisId, text, diagnosisType);

            return successExcludingPatientAndNurse(request, response);
        };
    }
}
