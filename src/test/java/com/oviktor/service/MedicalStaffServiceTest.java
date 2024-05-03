package com.oviktor.service;

import com.oviktor.dao.DoctorDao;
import com.oviktor.dao.UserDao;
import com.oviktor.dao.utils.Pagination;
import com.oviktor.dao.utils.Sorting;
import com.oviktor.dto.*;
import com.oviktor.entity.User;
import com.oviktor.enums.MedicineCategories;
import com.oviktor.enums.Roles;
import com.oviktor.mapper.UserMapper;
import com.oviktor.service.impl.MedicalStaffServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalStaffServiceTest {
    @Mock
    private UserDao userDao;
    @Mock
    private DoctorDao doctorDao;
    @Mock
    private UserMapper userMapper;
    @Mock
    private CreateUserDto createUserDto;
    @Mock
    private List<DoctorDto> doctorDtoList;
    @Mock
    private List<NurseDto> nurseDtoList;
    @Mock
    private DoctorCompactedDto doctorCompactedDto;
    private MedicalStaffService medicalStaffService;

    @BeforeEach
    void init() {
        medicalStaffService = new MedicalStaffServiceImpl(userDao, doctorDao, userMapper);
    }

    @Test
    void successCreateDoctorTest() {

        User user = User.builder()
                .id(1L)
                .firstName("Петро")
                .lastName("Гайдамак")
                .email("patient1@gmail.com")
                .phone("+380964781245")
                .usersPassword("")
                .role(Roles.DOCTOR)
                .dateOfBirth(LocalDate.of(1988, 5, 4))
                .isTreated(false)
                .build();

        doReturn(user)
                .when(userMapper)
                .mapDoctorDtoToUser(createUserDto);
        doReturn(1L)
                .when(userDao)
                .addUser(eq(user), anyBoolean());
        doNothing().when(doctorDao).addDoctor(eq(1L), eq(MedicineCategories.PEDIATRICIAN), anyBoolean());
        assertDoesNotThrow(() -> medicalStaffService.createDoctor(createUserDto, MedicineCategories.PEDIATRICIAN));
    }

    @Test
    void notSuccessCreateDoctorTest() {

        User user = User.builder()
                .id(1L)
                .firstName("Петро")
                .lastName("Гайдамак")
                .email("patient1@gmail.com")
                .phone("+380964781245")
                .usersPassword("")
                .role(Roles.DOCTOR)
                .dateOfBirth(LocalDate.of(1988, 5, 4))
                .isTreated(false)
                .build();

        doReturn(user)
                .when(userMapper)
                .mapDoctorDtoToUser(createUserDto);
        doReturn(1L)
                .when(userDao)
                .addUser(eq(user), anyBoolean());
        doThrow(new RuntimeException("The runtime exception was thrown"))
                .when(doctorDao)
                .addDoctor(eq(1L), eq(MedicineCategories.PEDIATRICIAN), anyBoolean());
        Throwable actualException = assertThrows(RuntimeException.class,
                () -> medicalStaffService.createDoctor(createUserDto, MedicineCategories.PEDIATRICIAN));
        assertEquals("The runtime exception was thrown", actualException.getMessage());
    }

    @Test
    void successGetDoctorByIdTest() {

        DoctorDto doctorDto = DoctorDto.builder()
                .id(4L)
                .firstName("Dare")
                .lastName("Clarkson")
                .email("doctor4@gmail.com")
                .phone("+1587456524")
                .dateOfBirth(LocalDate.of(1984, 9, 15))
                .medicineCategory(MedicineCategories.SURGEON)
                .numOfPatients(25)
                .build();
        doReturn(doctorDto).when(userDao).getDoctorById(4L);
        assertDoesNotThrow(() -> medicalStaffService.getDoctorById(4L));
    }

    @Test
    void notSuccessGetDoctorByIdTest() {

        doThrow(new RuntimeException("The runtime exception was thrown"))
                .when(userDao)
                .getDoctorById(4L);
        Throwable actualExceptions = assertThrows(RuntimeException.class,
                () -> medicalStaffService.getDoctorById(4L));
        assertEquals("The runtime exception was thrown", actualExceptions.getMessage());

    }

    @Test
    void successGetNurseByIdTest() {

        NurseDto nurseDto = NurseDto.builder()
                .id(34L)
                .firstName("Dare")
                .lastName("Clarkson")
                .email("doctor4@gmail.com")
                .phone("+1587456524")
                .dateOfBirth(LocalDate.of(1984, 9, 15))
                .build();
        doReturn(nurseDto)
                .when(userDao)
                .getNurseById(34L);
        assertDoesNotThrow(() -> medicalStaffService.getNurseById(34L));
    }

    @Test
    void notSuccessGetNurseByIdTest() {

        doThrow(new RuntimeException("The runtime exception was thrown"))
                .when(userDao)
                .getNurseById(34L);
        Throwable actualException = assertThrows(RuntimeException.class,
                () -> medicalStaffService.getNurseById(34L));
        assertEquals("The runtime exception was thrown", actualException.getMessage());
    }

    @Test
    void successGetDoctorsSortedByNameTest() {

        PagedDoctorsDto pagedDoctorsDto = new PagedDoctorsDto(doctorDtoList, 5, 10);
        Sorting sorting = Sorting.ASC;
        Pagination pagination = Pagination.pageNum(5);
        doReturn(pagedDoctorsDto)
                .when(userDao)
                .getDoctorsSortedByName(sorting, null, pagination);
        assertDoesNotThrow(() -> medicalStaffService.getDoctorsSortedByName(sorting, null, pagination));

    }

    @Test
    void notSuccessGetDoctorsSortedByNameTest() {

        Sorting sorting = Sorting.ASC;
        Pagination pagination = Pagination.pageNum(5);
        doThrow(new RuntimeException("The runtime exception was thrown"))
                .when(userDao)
                .getDoctorsSortedByName(sorting, null, pagination);
        Throwable actualException = assertThrows(RuntimeException.class,
                () -> medicalStaffService.getDoctorsSortedByName(sorting, null, pagination));
        assertEquals("The runtime exception was thrown", actualException.getMessage());
    }

//    @Test
//    void successGetDoctorsByCategorySortedByNameTest() {
//        PagedDoctorsDto pagedDoctorsDto = new PagedDoctorsDto(doctorDtoList, 8, 9);
//        Sorting sorting = Sorting.DESC;
//        MedicineCategories medicineCategory = MedicineCategories.NEUROLOGISTS;
//        Pagination pagination = Pagination.pageNum(5);
//        doReturn(pagedDoctorsDto)
//                .when(userDao)
//                .getDoctorsByCategoryId(medicineCategory, sorting, pagination);
//        assertDoesNotThrow(() -> medicalStaffService.getDoctorsByCategorySortedByName(medicineCategory, sorting, pagination));
//    }

//    @Test
//    void notSuccessGetDoctorsByCategorySortedByNameTest() {
//        Sorting sorting = Sorting.DESC;
//        MedicineCategories medicineCategory = MedicineCategories.NEUROLOGISTS;
//        Pagination pagination = Pagination.pageNum(5);
//        doThrow(new RuntimeException("The runtime exception was thrown"))
//                .when(userDao)
//                .getDoctorsByCategoryId(medicineCategory, sorting, pagination);
//        Throwable actualException = assertThrows(RuntimeException.class,
//                () -> medicalStaffService.getDoctorsByCategorySortedByName(medicineCategory, sorting, pagination));
//        assertEquals("The runtime exception was thrown", actualException.getMessage());
//    }


    @Test
    void successGetDoctorsByNumberOfPatientsTest() {
        PagedDoctorsDto pagedDoctorsDto = new PagedDoctorsDto(doctorDtoList, 1, 2);
        Sorting sorting = Sorting.ASC;
        Pagination pagination = Pagination.pageNum(5);
        doReturn(pagedDoctorsDto)
                .when(userDao)
                .getDoctorsSortedByNumberOfPatients(sorting, null, pagination);
        assertDoesNotThrow(() -> medicalStaffService.getDoctorsByNumberOfPatients(sorting, null, pagination));
    }

    @Test
    void notSuccessGetDoctorsByNumberOfPatientsTest() {
        Sorting sorting = Sorting.DESC;
        Pagination pagination = Pagination.pageNum(5);
        doThrow(new RuntimeException("The runtime exception was thrown"))
                .when(userDao)
                .getDoctorsSortedByNumberOfPatients(sorting, null, pagination);
        Throwable actualException = assertThrows(RuntimeException.class,
                () -> medicalStaffService.getDoctorsByNumberOfPatients(sorting, null, pagination));
        assertEquals("The runtime exception was thrown", actualException.getMessage());
    }

    @Test
    void successCreateNurseTest() {
        User nurse = User.builder()
                .id(78L)
                .firstName("Петро")
                .lastName("Гайдамак")
                .email("patient1@gmail.com")
                .phone("+380964781245")
                .usersPassword("")
                .role(Roles.NURSE)
                .dateOfBirth(LocalDate.of(1988, 5, 4))
                .isTreated(false)
                .build();
        doReturn(nurse)
                .when(userMapper)
                .mapNurseDtoToUser(createUserDto);
        doReturn(78L)
                .when(userDao)
                .addUser(nurse);
        assertDoesNotThrow(() -> medicalStaffService.createNurse(createUserDto));

    }

    @Test
    void notSuccessCreateNurseTest() {
        User nurse = User.builder()
                .id(1L)
                .firstName("Петро")
                .lastName("Гайдамак")
                .email("patient1@gmail.com")
                .phone("+380964781245")
                .usersPassword("")
                .role(Roles.NURSE)
                .dateOfBirth(LocalDate.of(1988, 5, 4))
                .isTreated(false)
                .build();
        doReturn(nurse)
                .when(userMapper)
                .mapNurseDtoToUser(createUserDto);
        doThrow(new RuntimeException("The runtime exception was thrown"))
                .when(userDao)
                .addUser(nurse);
        Throwable actualException = assertThrows(RuntimeException.class, () -> medicalStaffService.createNurse(createUserDto));
        assertEquals("The runtime exception was thrown", actualException.getMessage());
    }

    @Test
    void successGetNursesSortedByName() {
        PagedNursesDto pagedNursesDto = new PagedNursesDto(nurseDtoList, 5, 9);
        Sorting sorting = Sorting.DESC;
        Pagination pagination = Pagination.pageNum(5);
        doReturn(pagedNursesDto)
                .when(userDao)
                .getNursesSortedByName(sorting, pagination);
        assertDoesNotThrow(() -> medicalStaffService.getNursesSortedByName(sorting, pagination));
    }

    @Test
    void notSuccessGetNursesSortedByName() {
        Sorting sorting = Sorting.ASC;
        Pagination pagination = Pagination.pageNum(4);
        doThrow(new RuntimeException("The runtime exception was thrown"))
                .when(userDao)
                .getNursesSortedByName(sorting, pagination);
        Throwable actualException = assertThrows(RuntimeException.class,
                () -> medicalStaffService.getNursesSortedByName(sorting, pagination));
        assertEquals("The runtime exception was thrown", actualException.getMessage());
    }

    @Test
    void successGetDoctorsByPatientId() {

        DoctorCompactedDto compactedDto1 = DoctorCompactedDto.builder()
                .id(20L)
                .firstName("Василь")
                .lastName("Романенко")
                .medicineCategory(MedicineCategories.SURGEON)
                .build();
        DoctorCompactedDto compactedDto2 = DoctorCompactedDto.builder()
                .id(21L)
                .firstName("Ганна")
                .lastName("Пленковецька")
                .medicineCategory(MedicineCategories.PEDIATRICIAN)
                .build();
        DoctorCompactedDto compactedDto3 = DoctorCompactedDto.builder()
                .id(22L)
                .firstName("Світлана")
                .lastName("Вовк")
                .medicineCategory(MedicineCategories.SURGEON)
                .build();
        List<DoctorCompactedDto> doctorCompactedDtoList = List.of(compactedDto1,compactedDto2,compactedDto3);
        doReturn(doctorCompactedDtoList)
                .when(userDao)
                .getDoctorsByPatientId(33L);
        assertDoesNotThrow(() -> medicalStaffService.getDoctorsByPatientId(33L));
    }

    @Test
    void notSuccessGetDoctorsByPatientId() {
        doThrow(new RuntimeException("The runtime exception was thrown"))
                .when(userDao)
                .getDoctorsByPatientId(eq(43L));
        Throwable actualException = assertThrows(RuntimeException.class,
                () -> medicalStaffService.getDoctorsByPatientId(43L));
        assertEquals("The runtime exception was thrown", actualException.getMessage());
    }
}