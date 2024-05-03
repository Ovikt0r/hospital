package com.oviktor.service.impl;

import com.oviktor.annotation.Transactional;
import com.oviktor.dao.AppointmentDao;
import com.oviktor.dao.DiagnosisDao;
import com.oviktor.dao.UserDao;
import com.oviktor.dao.UsersDoctorsDao;
import com.oviktor.dao.utils.Pagination;
import com.oviktor.dao.utils.Sorting;
import com.oviktor.dto.*;
import com.oviktor.entity.User;
import com.oviktor.enums.AppointmentTypes;
import com.oviktor.enums.DiagnosisTypes;
import com.oviktor.mapper.UserMapper;
import com.oviktor.service.PatientService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final UserDao userDao;
    private final AppointmentDao appointmentDao;
    private final DiagnosisDao diagnosisDao;
    private final UsersDoctorsDao usersDoctorsDao;
    private final UserMapper userMapper;

    @Transactional
    public void createPatient(CreateUserDto patientDto, Long doctorId) {
        User patient = userMapper.mapPatientDtoToUser(patientDto);
        long patientId = userDao.addUser(patient, true);
        usersDoctorsDao.addPatientToDoctor(patientId, doctorId, true);
    }

    public PatientDto getPatientById(Long id) {
        return userDao.getPatientById(id);
    }

    public PagedPatientsDto getPatientsSortedByName(Sorting sorting, Pagination pagination) {
        return userDao.getPatientsSortedByName(sorting, pagination);
    }

    public PagedPatientsDto getPatientsSortedByDateOfBirth(Sorting sorting, Pagination pagination) {
        return userDao.getPatientsSortedByDateOfBirth(sorting, pagination);
    }

    public PagedPatientsDto getPatientsByDoctorSortedByName(long doctorId, Sorting sorting, Pagination pagination) {
        return userDao.getPatientsByDoctorIdSortedByName(doctorId, sorting, pagination);
    }

    public PagedPatientsDto getPatientsByDoctorSortedByDateOfBirth(long doctorId, Sorting sorting, Pagination pagination) {
        return userDao.getPatientsByDoctorIdSortedByDateOfBirth(doctorId, sorting, pagination);
    }
    public void dischargePatient(long patientId, long doctorId, String diagnosisText) {
        dischargePatient(patientId,doctorId,diagnosisText,LocalDateTime.now());
    }
    public void dischargePatient(long patientId, long doctorId, String diagnosisText,LocalDateTime localDateTime) {
        PatientDto patient = userDao.getPatientById(patientId);
        boolean patientIsTreatedByDoctor = usersDoctorsDao.patientIsTreatedByDoctor(patientId, doctorId);
        if (!patientIsTreatedByDoctor) {
            throw new RuntimeException();
        }
        if (patient == null) {
            throw new RuntimeException();
        }
        if (!patient.getIsTreated()) {
            throw new RuntimeException();
        }

        AppointmentDto appointment = appointmentDao.getLastAppointmentByPatientId(patientId);
        if (appointment == null || !appointment.getAppointmentType().equals(AppointmentTypes.CONSULTATION) || appointment.getDiagnosis() == null) {
            Long diagnosisId = diagnosisDao.addDiagnosis(diagnosisText, DiagnosisTypes.TREATING_IS_FINISHED);
            appointmentDao.addAppointment(
                    MakeAppointmentDto.builder()
                            .patientId(patientId)
                            .doctorId(doctorId)
                            .appointmentType(AppointmentTypes.CONSULTATION)
                            .appointmentDateTime(localDateTime)
                            .build(),
                    diagnosisId
            );
        }
        if (appointment != null && appointment.getDiagnosis() == null) {
            Long diagnosisId = diagnosisDao.addDiagnosis(diagnosisText, DiagnosisTypes.TREATING_IS_FINISHED);
            appointmentDao.addDiagnosis(appointment.getId(), diagnosisId);
        }
        userDao.updatePatientTreatment(patientId, false);
        usersDoctorsDao.deleteAllByPatientId(patientId);
    }

    public boolean checkPatientIsTreatedByDoctor(Long patientId, Long doctorId) {
        return usersDoctorsDao.patientIsTreatedByDoctor(patientId, doctorId);
    }
}

