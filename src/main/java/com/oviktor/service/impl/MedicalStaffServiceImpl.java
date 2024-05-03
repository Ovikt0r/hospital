package com.oviktor.service.impl;

import com.oviktor.annotation.Transactional;
import com.oviktor.dao.DoctorDao;
import com.oviktor.dao.UserDao;
import com.oviktor.dao.utils.Pagination;
import com.oviktor.dao.utils.Sorting;
import com.oviktor.dto.*;
import com.oviktor.entity.User;
import com.oviktor.enums.MedicineCategories;
import com.oviktor.mapper.UserMapper;
import com.oviktor.service.MedicalStaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class MedicalStaffServiceImpl implements MedicalStaffService {

    private final UserDao userDao;
    private final DoctorDao doctorDao;
    private final UserMapper userMapper;

    @Transactional
    public void createDoctor(CreateUserDto createUserDto, MedicineCategories medicineCategory) {
        User user = userMapper.mapDoctorDtoToUser(createUserDto);
        Long userId = userDao.addUser(user, true);
        doctorDao.addDoctor(userId, medicineCategory, true);
    }

    public DoctorDto getDoctorById(Long doctorId) {
        return userDao.getDoctorById(doctorId);
    }

    public NurseDto getNurseById(Long nurseId) {
        return userDao.getNurseById(nurseId);
    }

    public AdminDto getAdminById(Long adminId) {
        return userDao.getAdminById(adminId);
    }

    public PagedDoctorsDto getDoctorsSortedByName(Sorting sorting, MedicineCategories filterByCategory, Pagination pagination) {
        return userDao.getDoctorsSortedByName(sorting, filterByCategory, pagination);
    }

    public PagedDoctorsDto getDoctorsByNumberOfPatients(Sorting sorting, MedicineCategories filterByCategory, Pagination pagination) {
        return userDao.getDoctorsSortedByNumberOfPatients(sorting, filterByCategory, pagination);
    }

    public void createNurse(CreateUserDto createUserDto) {
        User user = userMapper.mapNurseDtoToUser(createUserDto);
        userDao.addUser(user);
    }

    public PagedNursesDto getNursesSortedByName(Sorting sorting, Pagination pagination) {
        return userDao.getNursesSortedByName(sorting, pagination);
    }

    public List<DoctorCompactedDto> getDoctorsByPatientId(Long patientId) {
        return userDao.getDoctorsByPatientId(patientId);
    }
}
