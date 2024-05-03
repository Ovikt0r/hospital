package com.oviktor.service;

import com.oviktor.dao.utils.Pagination;
import com.oviktor.dao.utils.Sorting;
import com.oviktor.dto.*;
import com.oviktor.entity.Appointment;

public interface AppointmentService {

    void makeAppointment(UserInfo currentUserInfo, MakeAppointmentDto makeAppointmentDto);

    void updateAppointment(UserInfo currentUserInfo, UpdateAppointmentDto updateAppointmentDto);

    void cancelAppointment(long appointmentId);

    PagedAppointmentsDto getAppointmentsByPatientId(Long patientId, Sorting sorting, Pagination pagination);

    PagedAppointmentsDto getAppointmentsByDoctorId(Long doctorId, Sorting sorting, Pagination pagination);

    AppointmentDto getAppointmentWithDiagnosisById(Long appointmentId);

    AppointmentDto getLastAppointmentByTreatedPatientId(long patientId);

    Appointment getAppointmentById(Long appointmentId);
}
