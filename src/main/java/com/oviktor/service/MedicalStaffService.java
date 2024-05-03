package com.oviktor.service;

import com.oviktor.dao.utils.Pagination;
import com.oviktor.dao.utils.Sorting;
import com.oviktor.dto.*;
import com.oviktor.enums.MedicineCategories;

import java.util.List;

public interface MedicalStaffService {

    void createDoctor(CreateUserDto createUserDto, MedicineCategories medicineCategory);

    DoctorDto getDoctorById(Long doctorId);

    NurseDto getNurseById(Long nurseId);

    AdminDto getAdminById(Long adminId);

    PagedDoctorsDto getDoctorsSortedByName(Sorting sorting, MedicineCategories filterByCategory, Pagination pagination);

    PagedDoctorsDto getDoctorsByNumberOfPatients(Sorting sorting, MedicineCategories filterByCategory, Pagination pagination);

    void createNurse(CreateUserDto createUserDto);

    PagedNursesDto getNursesSortedByName(Sorting sorting, Pagination pagination);

    List<DoctorCompactedDto> getDoctorsByPatientId(Long patientId);
}
