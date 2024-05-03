package com.oviktor.service;

import com.oviktor.dao.utils.Pagination;
import com.oviktor.dao.utils.Sorting;
import com.oviktor.dto.CreateUserDto;
import com.oviktor.dto.PagedPatientsDto;
import com.oviktor.dto.PatientDto;

import java.time.LocalDateTime;

public interface PatientService {

    void createPatient(CreateUserDto patientDto, Long doctorId);

    PatientDto getPatientById(Long id);

    PagedPatientsDto getPatientsSortedByName(Sorting sorting, Pagination pagination);

    PagedPatientsDto getPatientsSortedByDateOfBirth(Sorting sorting, Pagination pagination);

    PagedPatientsDto getPatientsByDoctorSortedByName(long doctorId, Sorting sorting, Pagination pagination);

    PagedPatientsDto getPatientsByDoctorSortedByDateOfBirth(long doctorId, Sorting sorting, Pagination pagination);

    void dischargePatient(long patientId, long doctorId, String diagnosisText);

    void dischargePatient(long patientId, long doctorId, String diagnosisText, LocalDateTime localDateTime);

    boolean checkPatientIsTreatedByDoctor(Long patientId, Long doctorId);
}


