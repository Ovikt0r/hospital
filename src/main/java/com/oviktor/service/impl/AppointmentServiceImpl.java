package com.oviktor.service.impl;

import com.oviktor.annotation.Transactional;
import com.oviktor.dao.AppointmentDao;
import com.oviktor.dao.UsersDoctorsDao;
import com.oviktor.dao.utils.Pagination;
import com.oviktor.dao.utils.Sorting;
import com.oviktor.dto.*;
import com.oviktor.entity.Appointment;
import com.oviktor.enums.AppointmentPermissions;
import com.oviktor.enums.Roles;
import com.oviktor.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentDao appointmentDao;
    private final UsersDoctorsDao usersDoctorsDao;

    @Transactional
    public void makeAppointment(UserInfo currentUserInfo, MakeAppointmentDto makeAppointmentDto) {
        if (AppointmentPermissions.getByRole(Roles.valueOf(currentUserInfo.role())).stream()
                .noneMatch(value -> value.id() == makeAppointmentDto.appointmentType().getId())) {
            log.error("Role {} doesn't have permission to create the appointment of {} type "
                    ,currentUserInfo.role(),makeAppointmentDto.appointmentType().name());
            throw new RuntimeException();
        }
        if (makeAppointmentDto.appointmentDateTime().isBefore(LocalDateTime.now())) {
            log.error("The date/time of the appointment was specify in the past.You must specify date/time in the future");
            throw new RuntimeException();
        }
        if (!usersDoctorsDao.patientIsTreatedByDoctor(makeAppointmentDto.patientId(), makeAppointmentDto.doctorId())) {
            usersDoctorsDao.addPatientToDoctor(makeAppointmentDto.patientId(), makeAppointmentDto.doctorId(),true);
        }
        appointmentDao.addAppointment(makeAppointmentDto);
    }

    @Transactional
    public void updateAppointment(UserInfo currentUserInfo, UpdateAppointmentDto updateAppointmentDto) {
        if (AppointmentPermissions.getByRole(Roles.valueOf(currentUserInfo.role())).stream()
                .noneMatch(value -> value.id() == updateAppointmentDto.appointmentType().getId())) {
            log.error("Role {} doesn't have permission to interact with appointment type = {}"
                    ,currentUserInfo.role(),updateAppointmentDto.appointmentType().name());
            throw new RuntimeException();
        }
        if (updateAppointmentDto.appointmentDateTime().isBefore(LocalDateTime.now())) {
            log.error("The date/time of the appointment was specify in the past.You must specify date/time in the future");
            throw new RuntimeException();
        }
        appointmentDao.updateAppointment(updateAppointmentDto);
    }

    public void cancelAppointment(long appointmentId) {
        appointmentDao.cancelAppointment(appointmentId);
    }

    public PagedAppointmentsDto getAppointmentsByPatientId(Long patientId, Sorting sorting, Pagination pagination) {
        return appointmentDao.getAppointmentsByPatientId(patientId, sorting, pagination);
    }

    public PagedAppointmentsDto getAppointmentsByDoctorId(Long doctorId, Sorting sorting, Pagination pagination) {
        return appointmentDao.getAppointmentsByDoctorId(doctorId, sorting, pagination);
    }

    public AppointmentDto getAppointmentWithDiagnosisById(Long appointmentId) {
        return appointmentDao.getAppointmentWithDiagnosisById(appointmentId);
    }

    public Appointment getAppointmentById(Long appointmentId) {
        return appointmentDao.getAppointmentById(appointmentId);
    }

    public AppointmentDto getLastAppointmentByTreatedPatientId(long patientId){
        return appointmentDao.getLastAppointmentByTreatedPatientId(patientId);
    }
}

