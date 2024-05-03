package com.oviktor.controller.action;

import com.oviktor.controller.action.actions.impl.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

public class ActionFactory {

    private static ActionFactory factory = new ActionFactory();
    private static final Map<String, Actionable> actions = new HashMap<>();

    private ActionFactory() {
    }

    /**
     * Singleton pattern
     */
    public static ActionFactory actionFactory() {
        if (factory == null) {
            factory = new ActionFactory();
        }
        return factory;
    }

    static {
        setAuthenticationActions();
        setUpDoctorActions();
        setUpPatientActions();
        setUpNurseActions();
        setAppointmentActions();
        setDiagnosisActions();
        setStatusActions();
    }

    private static void setAuthenticationActions() {
        actions.put(Commands.LOGIN_PAGE, AuthenticationAction.showLoginPage());
        actions.put(Commands.LOGIN, AuthenticationAction.login());
        actions.put(Commands.LOGOUT, AuthenticationAction.logout());
        actions.put(Commands.MAIN, AuthenticationAction.showMainPage());
    }

    private static void setUpDoctorActions() {
        actions.put(Commands.DOCTORS, ShowPagedDoctorsAction.showPagedDoctorsPage());
        actions.put(Commands.CREATE_DOCTOR_PAGE, DoctorAction.showCreateDoctorPage());
        actions.put(Commands.CREATE_DOCTOR, DoctorAction.createDoctor());
        actions.put(Commands.DOCTOR, DoctorAction.showDoctorCardPage());
        actions.put(Commands.DOCTOR_PATIENTS, ShowPagedDoctorPatientsAction.getDoctorPatients());
    }

    private static void setUpPatientActions() {
        actions.put(Commands.PATIENTS, ShowPagedPatientsAction.showPagedPatientsPage());
        actions.put(Commands.PATIENT_CARD, PatientAction.showPatientCard());
        actions.put(Commands.CREATE_PATIENT_PAGE, CreatePatientAction.showCreatePatientPage());
        actions.put(Commands.CREATE_PATIENT, CreatePatientAction.createPatient());
        actions.put(Commands.CHOOSE_DOCTOR_FOR_PATIENT_CREATION_PAGE, CreatePatientAction.showChooseDoctorForPatientCreationPage());
        actions.put(Commands.DISCHARGE_PAGE, PatientAction.dischargePatientPage());
        actions.put(Commands.DISCHARGE, PatientAction.dischargePatient());
        actions.put(Commands.PRINT_DISCHARGE_FORM, GeneratePdfAction.createPdfDischargeForm());
    }

    private static void setUpNurseActions() {
        actions.put(Commands.NURSES, ShowPagedNursesAction.showPagedNurses());
        actions.put(Commands.NURSE_CARD, NurseAction.showNurseCard());
        actions.put(Commands.CREATE_NURSE, NurseAction.createNurse());
        actions.put(Commands.CREATE_NURSE_PAGE, NurseAction.showCreateNursePage());
    }

    private static void setAppointmentActions() {
        actions.put(Commands.DOCTOR_APPOINTMENTS, ShowPagedAppointmentsAction.showDoctorAppointmentsPage());
        actions.put(Commands.PATIENT_APPOINTMENTS, ShowPagedAppointmentsAction.showPatientAppointmentsPage());
        actions.put(Commands.APPOINTMENT_CARD, ShowPagedAppointmentsAction.showAppointmentCardPage());
        actions.put(Commands.CANCEL_APPOINTMENT, AppointmentAction.cancelAppointment());
        actions.put(Commands.UPDATE_APPOINTMENT_PAGE, AppointmentAction.showUpdateAppointmentPage());
        actions.put(Commands.UPDATE_APPOINTMENT, AppointmentAction.updateAppointment());
        actions.put(Commands.MAKE_APPOINTMENT, CreateAppointmentAction.createAppointment());
        actions.put(Commands.MAKE_APPOINTMENT_FOR_DOCTOR_PAGE, CreateAppointmentAction.CreateAppointmentForDoctorAction.showCreateAppointmentForDoctorPage());
        actions.put(Commands.MAKE_APPOINTMENT_FOR_PATIENT_PAGE, CreateAppointmentAction.CreateAppointmentForPatientAction.showCreateAppointmentForPatientPage());
        actions.put(Commands.CHOOSE_DOCTOR_FOR_APPOINTMENT_CREATION_PAGE, CreateAppointmentAction.CreateAppointmentForPatientAction.showChooseDoctorForAppointmentCreationPage());
        actions.put(Commands.CHOOSE_PATIENT_FOR_APPOINTMENT_CREATION_PAGE, CreateAppointmentAction.CreateAppointmentForDoctorAction.showChoosePatientForAppointmentCreationPage());
    }

    private static void setDiagnosisActions() {
        actions.put(Commands.SET_DIAGNOSIS_PAGE, DiagnosisAction.showSetDiagnosisPage());
        actions.put(Commands.SET_DIAGNOSIS, DiagnosisAction.setDiagnosis());
        actions.put(Commands.UPDATE_DIAGNOSIS_PAGE, DiagnosisAction.showUpdateDiagnosisPage());
        actions.put(Commands.UPDATE_DIAGNOSIS, DiagnosisAction.updateDiagnosis());
    }

    private static void setStatusActions() {
        actions.put(Commands.StatusCommands.SUCCESS, StatusAction.showSuccessPage());
        actions.put(Commands.StatusCommands.ERROR, StatusAction.showErrorPage());
        actions.put(Commands.StatusCommands.FORBIDDEN, StatusAction.showForbiddenPage());
        actions.put(Commands.StatusCommands.NOT_FOUND, StatusAction.showNotFoundPage());
    }

    public Actionable getAction(HttpServletRequest request) {
        String action = request.getParameter("action");
        return actions.get(action);
    }
}
