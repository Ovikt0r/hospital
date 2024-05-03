package com.oviktor.controller.action;

import java.util.List;

public class Commands {

    public static final String MAIN = "mainPage";

    public static final String LOGIN = "login";
    public static final String LOGIN_PAGE = "loginPage";
    public static final String LOGOUT = "logout";

    public static final String DOCTORS = "doctors";
    public static final String CREATE_DOCTOR_PAGE = "createDoctorPage";
    public static final String CREATE_DOCTOR = "createDoctor";
    public static final String DOCTOR = "doctor";
    public static final String DOCTOR_PATIENTS = "doctorPatients";
    public static final String PATIENTS = "patients";
    public static final String PATIENT_CARD = "patientCard";
    public static final String CREATE_PATIENT_PAGE = "createPatientPage";
    public static final String CREATE_PATIENT = "createPatient";
    public static final String CHOOSE_DOCTOR_FOR_PATIENT_CREATION_PAGE = "chooseDoctorForPatientCreationPage";
    public static final String DISCHARGE_PAGE = "dischargePage";
    public static final String DISCHARGE = "discharge";
    public static final String NURSES = "nurses";
    public static final String NURSE_CARD = "nurseCard";
    public static final String CREATE_NURSE = "createNurse";
    public static final String CREATE_NURSE_PAGE = "createNursePage";
    public static final String DOCTOR_APPOINTMENTS = "doctorAppointments";
    public static final String PATIENT_APPOINTMENTS = "patientAppointments";
    public static final String APPOINTMENT_CARD = "appointmentCard";
    public static final String CANCEL_APPOINTMENT = "cancelAppointment";
    public static final String UPDATE_APPOINTMENT_PAGE = "updateAppointmentPage";
    public static final String UPDATE_APPOINTMENT = "updateAppointment";
    public static final String MAKE_APPOINTMENT = "makeAppointment";
    public static final String MAKE_APPOINTMENT_FOR_DOCTOR_PAGE = "makeAppointmentForDoctorPage";
    public static final String MAKE_APPOINTMENT_FOR_PATIENT_PAGE = "makeAppointmentForPatientPage";
    public static final String CHOOSE_DOCTOR_FOR_APPOINTMENT_CREATION_PAGE = "chooseDoctorForAppointmentCreationPage";
    public static final String CHOOSE_PATIENT_FOR_APPOINTMENT_CREATION_PAGE = "choosePatientForAppointmentCreationPage";
    public static final String SET_DIAGNOSIS_PAGE = "setDiagnosisPage";
    public static final String SET_DIAGNOSIS = "setDiagnosis";
    public static final String UPDATE_DIAGNOSIS_PAGE = "updateDiagnosisPage";
    public static final String UPDATE_DIAGNOSIS = "updateDiagnosis";
    public static final String PRINT_DISCHARGE_FORM = "printPdfDischargeForm";

    public static class StatusCommands {
        public static final String ERROR = "error";
        public static final String FORBIDDEN = "forbidden";
        public static final String NOT_FOUND = "notFound";
        public static final String SUCCESS = "success";
        public static final String REDIRECT = "redirect";
    }

    public static List<String> getStatusCommands() {
        return List.of(MAIN, LOGIN, LOGOUT, StatusCommands.ERROR, StatusCommands.FORBIDDEN, StatusCommands.NOT_FOUND, StatusCommands.SUCCESS);
    }

    public static List<String> getAdminCommands() {
        return List.of(
                DOCTORS,
                CREATE_DOCTOR_PAGE,
                CREATE_DOCTOR,
                DOCTOR,
                DOCTOR_PATIENTS,
                PATIENTS,
                PATIENT_CARD,
                CREATE_PATIENT_PAGE,
                CREATE_PATIENT,
                CHOOSE_DOCTOR_FOR_PATIENT_CREATION_PAGE,
                DISCHARGE_PAGE,
                DISCHARGE,
                NURSES,
                NURSE_CARD,
                DOCTOR_APPOINTMENTS,
                PATIENT_APPOINTMENTS,
                APPOINTMENT_CARD,
                CANCEL_APPOINTMENT,
                UPDATE_APPOINTMENT_PAGE,
                UPDATE_APPOINTMENT,
                MAKE_APPOINTMENT,
                MAKE_APPOINTMENT_FOR_DOCTOR_PAGE,
                MAKE_APPOINTMENT_FOR_PATIENT_PAGE,
                CHOOSE_DOCTOR_FOR_APPOINTMENT_CREATION_PAGE,
                CHOOSE_PATIENT_FOR_APPOINTMENT_CREATION_PAGE,
                SET_DIAGNOSIS_PAGE,
                SET_DIAGNOSIS,
                UPDATE_DIAGNOSIS_PAGE,
                UPDATE_DIAGNOSIS,
                PRINT_DISCHARGE_FORM
        );
    }

    public static List<String> getDoctorCommands() {
        return List.of(
                DOCTOR,
                DOCTOR_PATIENTS,
                PATIENTS,
                PATIENT_CARD,
                CREATE_PATIENT_PAGE,
                CREATE_PATIENT,
                DISCHARGE_PAGE,
                DISCHARGE,
                NURSES,
                NURSE_CARD,
                DOCTOR_APPOINTMENTS,
                PATIENT_APPOINTMENTS,
                APPOINTMENT_CARD,
                CANCEL_APPOINTMENT,
                UPDATE_APPOINTMENT_PAGE,
                UPDATE_APPOINTMENT,
                MAKE_APPOINTMENT,
                MAKE_APPOINTMENT_FOR_DOCTOR_PAGE,
                MAKE_APPOINTMENT_FOR_PATIENT_PAGE,
                CHOOSE_PATIENT_FOR_APPOINTMENT_CREATION_PAGE,
                SET_DIAGNOSIS_PAGE,
                SET_DIAGNOSIS,
                UPDATE_DIAGNOSIS_PAGE,
                UPDATE_DIAGNOSIS,
                PRINT_DISCHARGE_FORM
        );
    }

    public static List<String> getNurseCommands() {
        return List.of(
                DOCTORS,
                DOCTOR,
                DOCTOR_PATIENTS,
                PATIENTS,
                PATIENT_CARD,
                CREATE_PATIENT_PAGE,
                CREATE_PATIENT,
                CHOOSE_DOCTOR_FOR_PATIENT_CREATION_PAGE,
                NURSE_CARD,
                DOCTOR_APPOINTMENTS,
                PATIENT_APPOINTMENTS,
                APPOINTMENT_CARD,
                CANCEL_APPOINTMENT,
                UPDATE_APPOINTMENT_PAGE,
                UPDATE_APPOINTMENT,
                MAKE_APPOINTMENT,
                MAKE_APPOINTMENT_FOR_DOCTOR_PAGE,
                MAKE_APPOINTMENT_FOR_PATIENT_PAGE,
                CHOOSE_DOCTOR_FOR_APPOINTMENT_CREATION_PAGE,
                CHOOSE_PATIENT_FOR_APPOINTMENT_CREATION_PAGE
        );
    }

    public static List<String> getPatientCommands() {
        return List.of(
                PATIENT_CARD,
                PATIENT_APPOINTMENTS,
                APPOINTMENT_CARD
        );
    }
}
