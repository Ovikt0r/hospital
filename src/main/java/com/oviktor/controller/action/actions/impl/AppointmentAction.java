package com.oviktor.controller.action.actions.impl;

import com.oviktor.ApplicationContext;
import com.oviktor.controller.action.Actionable;
import com.oviktor.controller.action.actions.AbstractAction;
import com.oviktor.dto.TypesDto;
import com.oviktor.dto.UpdateAppointmentDto;
import com.oviktor.entity.Appointment;
import com.oviktor.enums.AppointmentTypes;
import com.oviktor.enums.DiagnosisTypes;
import com.oviktor.service.AppointmentService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class AppointmentAction extends AbstractAction {

    private static AppointmentService appointmentService = ApplicationContext.getInstance().getAppointmentService();

    private static final String UPDATE_APPOINTMENT_PAGE = "update_appointment_page.jsp";

    AppointmentAction(AppointmentService appointmentService) {
        AppointmentAction.appointmentService = appointmentService;
    }


    public static Actionable cancelAppointment() {
        return (request, response) -> {
            long appointmentId = Long.parseLong(request.getParameter("appointmentId"));
            appointmentService.cancelAppointment(appointmentId);
            log.info("Appointment with {} id was canceled successfully",appointmentId);
            return successExcludingPatient(request, response);
        };
    }

    public static Actionable showUpdateAppointmentPage() {
        return (request, response) -> {
            log.info("Preparing to display the update appointment page");
            long appointmentId = Long.parseLong(request.getParameter("appointmentId"));

            Appointment currentAppointment = appointmentService.getAppointmentById(appointmentId);
            log.info("Setting the request attribute by the current appointment with {} id",appointmentId);
            request.setAttribute("currentAppointment", currentAppointment);
            log.info("The setting was successful");
            TypesDto currentAppointmentType = new TypesDto(
                    currentAppointment.getAppointmentType().getId(),
                    currentAppointment.getAppointmentType().toString()
            );
            log.info("Setting the request attribute by the current appointment type with {} name",currentAppointment.getAppointmentType().toString());
            request.setAttribute("selectedType", currentAppointmentType);
            log.info("The setting was successful");
            List<TypesDto> appointmentOptions = DiagnosisTypes.getAll();
            appointmentOptions.remove(currentAppointmentType);
            log.info("Setting the request attribute by the rest of appointment types for selection");
            request.setAttribute("appointmentOptions", appointmentOptions);
            log.info("The setting was successful");
            return excludePatient(UPDATE_APPOINTMENT_PAGE, request, response);
        };
    }

    public static Actionable updateAppointment() {
        return (request, response) -> {
            long appointmentId = Long.parseLong(request.getParameter("appointmentId"));
            log.info("Updating the appointment with {} id", appointmentId);
            appointmentService.updateAppointment(
                    getCurrentUserInfo(request),
                    new UpdateAppointmentDto(
                            appointmentId,
                            AppointmentTypes.getById(Long.parseLong(request.getParameter("appointmentType"))),
                            LocalDateTime.parse(request.getParameter("appointmentDateTime"))
                    )
            );
            log.info("Updating was successful");
            return successExcludingPatient(request, response);
        };
    }
}
