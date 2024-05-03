package com.oviktor.service;

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
import com.oviktor.enums.Roles;
import com.oviktor.mapper.UserMapper;
import com.oviktor.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private UserDao userDao;
    @Mock
    private AppointmentDao appointmentDao;
    @Mock
    private DiagnosisDao diagnosisDao;
    @Mock
    private UsersDoctorsDao usersDoctorsDao;
    @Mock
    private CreateUserDto patientDto;
    @Mock
    private UserMapper userMapper;
    @Mock
    List<PatientDto> patientDtos;

    PatientService patientService;


    @BeforeEach
    void init() {
        patientService = new PatientServiceImpl(
                userDao, appointmentDao, diagnosisDao, usersDoctorsDao, userMapper);

    }

    @Test
    public void successCreatPatientTest() {
        User patient1 = User.builder()
                .firstName("July")
                .lastName("Grey")
                .email("patient0505@hotmail.com")
                .phone("+380984585513")
                .dateOfBirth(LocalDate.of(1970, 7, 25))
                .usersPassword("Jdf74D5439dFs")
                .role(Roles.PATIENT)
                .isTreated(false)
                .build();

        doReturn(patient1)
                .when(userMapper)
                .mapPatientDtoToUser(patientDto);
        doReturn(1L)
                .when(userDao)
                .addUser(eq(patient1), anyBoolean());
        doNothing().when(usersDoctorsDao)
                .addPatientToDoctor(eq(1L), eq(24L), anyBoolean());
        assertDoesNotThrow(() -> this.patientService.createPatient(patientDto, 24L));
    }

    @Test
    public void notSuccessCreatePatientTest() {

        User patient1 = User.builder()
                .firstName("July")
                .lastName("Grey")
                .email("patient0505@hotmail.com")
                .phone("+380984585513")
                .dateOfBirth(LocalDate.of(1970, 7, 25))
                .usersPassword("Jdf74D5439dFs")
                .role(Roles.PATIENT)
                .isTreated(false)
                .build();

        User patient2 = User.builder()
                .firstName("Joe")
                .lastName("Docker")
                .email("doctor0124@hotmail.com")
                .phone("+380974585512")
                .dateOfBirth(LocalDate.of(1970, 7, 25))
                .usersPassword("dsf34D1455FEs")
                .role(Roles.PATIENT)
                .isTreated(false)
                .build();

        doReturn(patient1)
                .when(userMapper)
                .mapPatientDtoToUser(patientDto);
        doReturn(12L)
                .when(userDao)
                .addUser(eq(patient1), anyBoolean());
        doThrow(new RuntimeException("Exception was thrown in the second invocation method"))
                .when(usersDoctorsDao)
                .addPatientToDoctor(eq(12L), eq(24L), anyBoolean());
        Throwable expectedException1 =
                assertThrows(RuntimeException.class,
                        () -> patientService.createPatient(patientDto, 24L));

        doReturn(patient2)
                .when(userMapper)
                .mapPatientDtoToUser(patientDto);
        doThrow(new RuntimeException("Exception was thrown in the first invocation method"))
                .when(userDao).addUser(eq(patient2), anyBoolean());
        // doNothing().when(usersDoctorsDao).addPatientToDoctor(eq(17L), eq(39L), anyBoolean());
        Throwable expectedException2 = assertThrows(RuntimeException.class,
                () -> patientService.createPatient(patientDto, 39L));

        assertEquals("Exception was thrown in the second invocation method", expectedException1.getMessage());
        assertEquals("Exception was thrown in the first invocation method", expectedException2.getMessage());

    }

    @Test
    public void successGetPatientByIdTest() {

        PatientDto expectedPatientDto1 =
                PatientDto.builder()
                        .id(1L)
                        .firstName("Gerber")
                        .lastName("Wales")
                        .email("patient1@gmail.com")
                        .phone("+3809645781423")
                        .dateOfBirth(LocalDate.of(1988, 5, 4))
                        .isTreated(false)
                        .build();
        PatientDto expectedPatientDto2 =
                PatientDto.builder()
                        .id(4L)
                        .firstName("Samanta")
                        .lastName("Snow")
                        .email("patient4@gmail.com")
                        .phone("+380964568714")
                        .dateOfBirth(LocalDate.of(1965, 8, 29))
                        .isTreated(false)
                        .build();

        doReturn(expectedPatientDto1)
                .when(userDao)
                .getPatientById(eq(1L));
        doReturn(expectedPatientDto2)
                .when(userDao)
                .getPatientById(eq(4L));
        PatientDto actualPatientDto1 = patientService.getPatientById(1L);
        PatientDto actualPatientDto2 = patientService.getPatientById(4L);

        assertEquals(expectedPatientDto1, actualPatientDto1);
        assertEquals(expectedPatientDto2, actualPatientDto2);

    }

    @Test
    public void notSuccessGetPatientByIdTest() {

        String expectedExceptionMessage1 = "Exception was thrown by 2L id";
        String expectedExceptionMessage2 = "Exception was thrown by 5L id";

        doThrow(new RuntimeException(expectedExceptionMessage1))
                .when(userDao)
                .getPatientById(eq(2L));
        doThrow(new RuntimeException(expectedExceptionMessage2))
                .when(userDao)
                .getPatientById(eq(5L));
        Throwable actualException1 = assertThrows(RuntimeException.class,
                () -> patientService.getPatientById(2L));
        Throwable actualException2 = assertThrows(RuntimeException.class,
                () -> patientService.getPatientById(5L));

        assertEquals(expectedExceptionMessage1, actualException1.getMessage());
        assertEquals(expectedExceptionMessage2, actualException2.getMessage());
    }


    @Test
    public void successGetPatientsSortedByName() {
        Sorting sortingAscOrder = Sorting.ASC;
        Sorting sortingDescOrder = Sorting.DESC;
        Pagination pagination1 = Pagination.pageNum(5);
        Pagination pagination2 = Pagination.pageNum(6);

        PagedPatientsDto expectedPagedPatientsDtoAsc =
                new PagedPatientsDto(patientDtos, 5, 6);
        PagedPatientsDto expectedPagedPatientsDtoDesc =
                new PagedPatientsDto(patientDtos, 3, 6);

        doReturn(expectedPagedPatientsDtoAsc)
                .when(userDao)
                .getPatientsSortedByName(eq(sortingAscOrder), eq(pagination1));
        doReturn(expectedPagedPatientsDtoDesc)
                .when(userDao)
                .getPatientsSortedByName(eq(sortingDescOrder), eq(pagination2));

        PagedPatientsDto actualPagedPatientDtoAsc = patientService
                .getPatientsSortedByName(sortingAscOrder, pagination1);
        PagedPatientsDto actualPagedPatientDtoDesc = patientService
                .getPatientsSortedByName(sortingDescOrder, pagination2);

        assertEquals(expectedPagedPatientsDtoAsc, actualPagedPatientDtoAsc);
        assertEquals(expectedPagedPatientsDtoDesc, actualPagedPatientDtoDesc);
    }

    @Test
    public void notSuccessGetPatientsSortedByName() {
        Sorting sortingAscOrder = Sorting.ASC;
        Sorting sortingDescOrder = Sorting.DESC;
        String expectedExceptionMessage1 = "Exception was thrown with ASC sorting order";
        String expectedExceptionMessage2 = "Exception was thrown with DESC sorting order";
        Pagination pagination1 = Pagination.pageNum(9);
        Pagination pagination2 = Pagination.pageNum(10);
        doThrow(new RuntimeException(expectedExceptionMessage1))
                .when(userDao)
                .getPatientsSortedByName(eq(sortingAscOrder), eq(pagination1));
        doThrow(new RuntimeException(expectedExceptionMessage2))
                .when(userDao)
                .getPatientsSortedByName(eq(sortingDescOrder), eq(pagination2));

        Throwable actualException1 = assertThrows(RuntimeException.class,
                () -> patientService.getPatientsSortedByName(sortingAscOrder, pagination1));
        Throwable actualException2 = assertThrows(RuntimeException.class,
                () -> patientService.getPatientsSortedByName(sortingDescOrder, pagination2));

        assertEquals(expectedExceptionMessage1, actualException1.getMessage());
        assertEquals(expectedExceptionMessage2, actualException2.getMessage());

    }

    @Test
    public void successGetPatientsSortedByDateOfBirth() {
        Sorting sortingAsc = Sorting.ASC;
        Sorting sortingDesc = Sorting.DESC;
        Pagination pagination1 = Pagination.pageNum(6);
        Pagination pagination2 = Pagination.pageNum(3);
        PagedPatientsDto expectedPagedPatientsDtoAsc =
                new PagedPatientsDto(patientDtos, 6, 9);
        PagedPatientsDto expectedPagedPatientsDtoDesc =
                new PagedPatientsDto(patientDtos, 3, 4);
        doReturn(expectedPagedPatientsDtoAsc)
                .when(userDao)
                .getPatientsSortedByDateOfBirth(eq(sortingAsc), eq(pagination1));
        doReturn(expectedPagedPatientsDtoDesc)
                .when(userDao)
                .getPatientsSortedByDateOfBirth(eq(sortingDesc), eq(pagination2));
        PagedPatientsDto actualPagedPatientDtoAsc =
                patientService.getPatientsSortedByDateOfBirth(sortingAsc, pagination1);
        PagedPatientsDto actualPagedPatientDtoDesc =
                patientService.getPatientsSortedByDateOfBirth(sortingDesc, pagination2);

        assertEquals(expectedPagedPatientsDtoAsc, actualPagedPatientDtoAsc);
        assertEquals(expectedPagedPatientsDtoDesc, actualPagedPatientDtoDesc);

    }

    @Test
    public void notSuccessGetPatientsSortedByDateOfBirth() {
        Sorting sortingAscOrder = Sorting.ASC;
        Sorting sortingDescOrder = Sorting.DESC;
        String expectedExceptionMessage1 = "Exception was thrown with ASC sorting order";
        String expectedExceptionMessage2 = "Exception was thrown with DESC sorting order";

        Pagination pagination1 = Pagination.pageNum(40);
        Pagination pagination2 = Pagination.pageNum(99);
        doThrow(new RuntimeException(expectedExceptionMessage1))
                .when(userDao)
                .getPatientsSortedByDateOfBirth(eq(sortingAscOrder), eq(pagination1));
        doThrow(new RuntimeException(expectedExceptionMessage2))
                .when(userDao)
                .getPatientsSortedByDateOfBirth(eq(sortingDescOrder), eq(pagination2));

        Throwable actualException1 = assertThrows(RuntimeException.class,
                () -> patientService.getPatientsSortedByDateOfBirth(sortingAscOrder, pagination1));
        Throwable actualException2 = assertThrows(RuntimeException.class,
                () -> patientService.getPatientsSortedByDateOfBirth(sortingDescOrder, pagination2));

        assertEquals(expectedExceptionMessage1, actualException1.getMessage());
        assertEquals(expectedExceptionMessage2, actualException2.getMessage());

    }

    @Test
    public void successGetPatientsByDoctorSortedByName() {
        Sorting sortingAsc = Sorting.ASC;
        Sorting sortingDesc = Sorting.DESC;
        PagedPatientsDto expectedPagedPatientsDtoAsc =
                new PagedPatientsDto(patientDtos, 9, 16);
        PagedPatientsDto expectedPagedPatientsDtoDesc =
                new PagedPatientsDto(patientDtos, 8, 11);
        Pagination pagination1 = Pagination.pageNum(9);
        Pagination pagination2 = Pagination.pageNum(8);
        doReturn(expectedPagedPatientsDtoAsc)
                .when(userDao)
                .getPatientsByDoctorIdSortedByName(eq(1L), eq(sortingAsc), eq(pagination1));
        doReturn(expectedPagedPatientsDtoDesc)
                .when(userDao)
                .getPatientsByDoctorIdSortedByName(eq(10L), eq(sortingDesc), eq(pagination2));
        PagedPatientsDto actualPagedPatientDtoAsc = patientService
                .getPatientsByDoctorSortedByName(1L, sortingAsc, pagination1);
        PagedPatientsDto actualPagedPatientDtoDesc = patientService
                .getPatientsByDoctorSortedByName(10L, sortingDesc, pagination2);

        assertEquals(expectedPagedPatientsDtoAsc, actualPagedPatientDtoAsc);
        assertEquals(expectedPagedPatientsDtoDesc, actualPagedPatientDtoDesc);
    }

    @Test
    public void notSuccessGetPatientsByDoctorSortedByName() {
        Sorting sortingAscOrder = Sorting.ASC;
        Sorting sortingDescOrder = Sorting.DESC;
        String expectedExceptionMessage1 = "Exception was thrown with ASC sorting order";
        String expectedExceptionMessage2 = "Exception was thrown with DESC sorting order";
        Pagination pagination1 = Pagination.pageNum(13);
        Pagination pagination2 = Pagination.pageNum(15);
        doThrow(new RuntimeException(expectedExceptionMessage1))
                .when(userDao)
                .getPatientsByDoctorIdSortedByName(eq(2L), eq(sortingAscOrder), eq(pagination1));
        doThrow(new RuntimeException(expectedExceptionMessage2))
                .when(userDao)
                .getPatientsByDoctorIdSortedByName(eq(12L), eq(sortingDescOrder), eq(pagination2));

        Throwable actualException1 = assertThrows(RuntimeException.class,
                () -> patientService.getPatientsByDoctorSortedByName(2L, sortingAscOrder, pagination1));
        Throwable actualException2 = assertThrows(RuntimeException.class,
                () -> patientService.getPatientsByDoctorSortedByName(12L, sortingDescOrder, pagination2));

        assertEquals(expectedExceptionMessage1, actualException1.getMessage());
        assertEquals(expectedExceptionMessage2, actualException2.getMessage());
    }


    @Test
    public void successGetPatientsByDoctorSortedByDateOfBirth() {
        Sorting sortingAsc = Sorting.ASC;
        Sorting sortingDesc = Sorting.DESC;
        PagedPatientsDto expectedPagedPatientsDtoAsc =
                new PagedPatientsDto(patientDtos, 9, 9);
        PagedPatientsDto expectedPagedPatientsDtoDesc =
                new PagedPatientsDto(patientDtos, 7, 19);
        Pagination pagination1 = Pagination.pageNum(9);
        Pagination pagination2 = Pagination.pageNum(7);
        doReturn(expectedPagedPatientsDtoAsc)
                .when(userDao)
                .getPatientsByDoctorIdSortedByDateOfBirth(eq(1L), eq(sortingAsc), eq(pagination1));
        doReturn(expectedPagedPatientsDtoDesc)
                .when(userDao)
                .getPatientsByDoctorIdSortedByDateOfBirth(eq(10L), eq(sortingDesc), eq(pagination2));
        PagedPatientsDto actualPagedPatientDtoAsc = patientService
                .getPatientsByDoctorSortedByDateOfBirth(1L, sortingAsc, pagination1);
        PagedPatientsDto actualPagedPatientDtoDesc = patientService
                .getPatientsByDoctorSortedByDateOfBirth(10L, sortingDesc, pagination2);

        assertEquals(expectedPagedPatientsDtoAsc, actualPagedPatientDtoAsc);
        assertEquals(expectedPagedPatientsDtoDesc, actualPagedPatientDtoDesc);
    }

    @Test
    public void notSuccessGetPatientsByDoctorSortedByDateOfBirth() {
        Sorting sortingAscOrder = Sorting.ASC;
        Sorting sortingDescOrder = Sorting.DESC;
        String expectedExceptionMessage1 = "Exception was thrown with ASC sorting order";
        String expectedExceptionMessage2 = "Exception was thrown with DESC sorting order";
        Pagination pagination1 = Pagination.pageNum(6);
        Pagination pagination2 = Pagination.pageNum(7);
        doThrow(new RuntimeException(expectedExceptionMessage1))
                .when(userDao)
                .getPatientsByDoctorIdSortedByDateOfBirth(eq(3L), eq(sortingAscOrder), eq(pagination1));
        doThrow(new RuntimeException(expectedExceptionMessage2))
                .when(userDao)
                .getPatientsByDoctorIdSortedByDateOfBirth(eq(13L), eq(sortingDescOrder), eq(pagination2));

        Throwable actualException1 = assertThrows(RuntimeException.class,
                () -> patientService.getPatientsByDoctorSortedByDateOfBirth(3L, sortingAscOrder, pagination1));
        Throwable actualException2 = assertThrows(RuntimeException.class,
                () -> patientService.getPatientsByDoctorSortedByDateOfBirth(13L, sortingDescOrder, pagination2));

        assertEquals(expectedExceptionMessage1, actualException1.getMessage());
        assertEquals(expectedExceptionMessage2, actualException2.getMessage());
    }

    @Test
    public void successDischargePatientTest() {

        PatientDto returnedPatientDto =
                PatientDto.builder()
                        .id(25L)
                        .firstName("Gerber")
                        .lastName("Wales")
                        .email("patient1@gmail.com")
                        .phone("+3809645781423")
                        .dateOfBirth(LocalDate.of(1988, 5, 4))
                        .isTreated(true)
                        .build();
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.of(2022, 9, 18), LocalTime.of(13, 30));
        doReturn(returnedPatientDto).when(userDao).getPatientById(eq(25L));
        doReturn(true).when(usersDoctorsDao).patientIsTreatedByDoctor(eq(25L), eq(71L));
        AppointmentDto returnedAppointmentDto = AppointmentDto.builder()
                .id(115L)
                .patientId(25L)
                .patientFirstName("Carlo")
                .patientLastName("Nervetti")
                .doctorId(71L)
                .doctorFirstName("Лев")
                .doctorLastName("Фурман")
                .appointmentDateTime(dateTime)
                .appointmentType(AppointmentTypes.PROCEDURE)
                .canceled(false)
                .build();
        doReturn(returnedAppointmentDto)
                .when(appointmentDao)
                .getLastAppointmentByPatientId(eq(25L));
        doNothing().when(userDao).updatePatientTreatment(eq(25L), eq(false));
        doNothing().when(usersDoctorsDao).deleteAllByPatientId(25L);
        assertDoesNotThrow(() -> patientService.dischargePatient(25L, 71L, "some text"));
        verify(userDao, times(1)).getPatientById(25L);
        ;
        verify(userDao, times(1)).updatePatientTreatment(25L, false);
        verify(usersDoctorsDao, times(1)).patientIsTreatedByDoctor(25L, 71L);
        verify(usersDoctorsDao, times(1)).deleteAllByPatientId(25L);
    }

    @Test
    public void notSuccessDischargePatientTest1() {
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.of(2022, 9, 18), LocalTime.of(13, 30));
        LocalDate dateOfBirth = LocalDate.of(1988,5,23);
        AppointmentDto returnedAppointmentDto = AppointmentDto.builder()
                .id(115L)
                .patientId(25L)
                .patientFirstName("Carlo")
                .patientLastName("Nervetti")
                .doctorId(71L)
                .doctorFirstName("Лев")
                .doctorLastName("Фурман")
                .appointmentDateTime(dateTime)
                .appointmentType(AppointmentTypes.PROCEDURE)
                .canceled(false)
                .build();
        PatientDto patient = PatientDto.builder()
                .id(25L)
                .firstName("Carlo")
                .lastName("Nervetti")
                .email("donbeneton@gmail.com")
                .phone("+380984055547")
                .dateOfBirth(dateOfBirth)
                .isTreated(true)
                .build();
        doReturn(patient).when(userDao).getPatientById(eq(25L));
        doReturn(true).when(usersDoctorsDao).patientIsTreatedByDoctor(eq(25L), eq(71L));
        doReturn(returnedAppointmentDto)
                .when(appointmentDao)
                .getLastAppointmentByPatientId(eq(25L));
        doNothing().when(userDao).updatePatientTreatment(eq(25L), eq(false));
        doThrow(new RuntimeException()).when(usersDoctorsDao).deleteAllByPatientId(eq(25L));
        assertThrows(RuntimeException.class, () -> patientService.dischargePatient(25L, 71L, "some text"));
        verify(userDao, times(1)).getPatientById(25L);
        verify(usersDoctorsDao, times(1)).patientIsTreatedByDoctor(25L, 71L);
        verify(userDao, times(1)).updatePatientTreatment(25L, false);
        verify(usersDoctorsDao, times(1)).deleteAllByPatientId(25L);

    }

    @Test
    public void notSuccessDischargePatientTest2() {
        PatientDto returnedPatientDto =
                PatientDto.builder()
                        .id(25L)
                        .firstName("Gerber")
                        .lastName("Wales")
                        .email("patient1@gmail.com")
                        .phone("+3809645781423")
                        .dateOfBirth(LocalDate.of(1988, 5, 4))
                        .isTreated(true)
                        .build();
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.of(2022, 9, 18), LocalTime.of(13, 30));
        doReturn(returnedPatientDto).when(userDao).getPatientById(eq(25L));
        doReturn(false).when(usersDoctorsDao).patientIsTreatedByDoctor(eq(25L), eq(71L));
        AppointmentDto returnedAppointmentDto = AppointmentDto.builder()
                .id(115L)
                .patientId(25L)
                .patientFirstName("Carlo")
                .patientLastName("Nervetti")
                .doctorId(71L)
                .doctorFirstName("Лев")
                .doctorLastName("Фурман")
                .appointmentDateTime(dateTime)
                .appointmentType(AppointmentTypes.PROCEDURE)
                .canceled(false)
                .build();
//        doReturn(returnedAppointmentDto)
//                .when(appointmentDao)
//                .getLastAppointmentByPatientId(eq(25L));
//        doNothing().when(userDao).updatePatientTreatment(eq(25L), eq(false));
//        doNothing().when(usersDoctorsDao).deleteAllByPatientId(25L);
        assertThrows(RuntimeException.class, () -> patientService.dischargePatient(25L, 71L, "some text"));
        verify(userDao, times(1)).getPatientById(25L);
        verify(usersDoctorsDao, times(1)).patientIsTreatedByDoctor(25L, 71L);
        verify(usersDoctorsDao, never()).deleteAllByPatientId(25L);
        verify(userDao, never()).updatePatientTreatment(25L, false);
    }

    @Test
    public void notSuccessDischargePatientTest3() {
        PatientDto returnedPatientDto =
                PatientDto.builder()
                        .id(25L)
                        .firstName("Gerber")
                        .lastName("Wales")
                        .email("patient1@gmail.com")
                        .phone("+3809645781423")
                        .dateOfBirth(LocalDate.of(1988, 5, 4))
                        .isTreated(false)
                        .build();
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.of(2022, 9, 18), LocalTime.of(13, 30));
        doReturn(returnedPatientDto).when(userDao).getPatientById(eq(25L));
        doReturn(true).when(usersDoctorsDao).patientIsTreatedByDoctor(eq(25L), eq(71L));
        AppointmentDto returnedAppointmentDto = AppointmentDto.builder()
                .id(115L)
                .patientId(25L)
                .patientFirstName("Carlo")
                .patientLastName("Nervetti")
                .doctorId(71L)
                .doctorFirstName("Лев")
                .doctorLastName("Фурман")
                .appointmentDateTime(dateTime)
                .appointmentType(AppointmentTypes.PROCEDURE)
                .canceled(false)
                .build();
//        doReturn(returnedAppointmentDto)
//                .when(appointmentDao)
//                .getLastAppointmentByPatientId(eq(25L));
//        doNothing().when(userDao).updatePatientTreatment(eq(25L), eq(false));
//        doNothing().when(usersDoctorsDao).deleteAllByPatientId(25L);
        assertThrows(RuntimeException.class, () -> patientService.dischargePatient(25L, 71L, "some text"));
        verify(userDao, times(1)).getPatientById(25L);
        verify(usersDoctorsDao, times(1)).patientIsTreatedByDoctor(25L, 71L);
        verify(usersDoctorsDao, never()).deleteAllByPatientId(25L);
        verify(userDao, never()).updatePatientTreatment(25L, false);
    }

   /* @Test
    public void successDischargePatientTest4() {
        String diagnosisText = "Some text";
        Long patientId = 25L;
        Long doctorId = 71L;
        Long diagnosisId = 13L;
        LocalDateTime localDate = LocalDateTime.now();
        MakeAppointmentDto appointmentDto = MakeAppointmentDto.builder()
                .patientId(patientId)
                .doctorId(doctorId)
                .appointmentType(AppointmentTypes.CONSULTATION)
                .appointmentDateTime(localDate)
                .build();

        PatientDto returnedPatientDto =
                PatientDto.builder()
                        .id(patientId)
                        .firstName("Gerber")
                        .lastName("Wales")
                        .email("patient1@gmail.com")
                        .phone("+3809645781423")
                        .dateOfBirth(LocalDate.of(1988, 5, 4))
                        .isTreated(true)
                        .build();
        LocalDateTime dateTime = LocalDateTime.of(LocalDate.of(2022, 9, 18), LocalTime.of(13, 30));
        doReturn(returnedPatientDto).when(userDao).getPatientById(eq(patientId));
        doReturn(true).when(usersDoctorsDao).patientIsTreatedByDoctor(eq(patientId), eq(doctorId));
        AppointmentDto returnedAppointmentDto = AppointmentDto.builder()
                .id(115L)
                .patientId(patientId)
                .patientFirstName("Carlo")
                .patientLastName("Nervetti")
                .doctorId(doctorId)
                .doctorFirstName("Лев")
                .doctorLastName("Фурман")
                .appointmentDate(dateTime)
                .diagnosis(null)
                .appointmentType(AppointmentTypes.PROCEDURE)
                .canceled(false)
                .build();
        doReturn(returnedAppointmentDto)
                .when(appointmentDao)
                .getLastAppointmentByPatientId(eq(patientId));
        doReturn(diagnosisId).when(diagnosisDao).addDiagnosis(eq(diagnosisText), eq(DiagnosisTypes.TREATING_IS_FINISHED));
        doReturn(115L).when(appointmentDao).addAppointment(eq(MakeAppointmentDto.builder()
                        .patientId(patientId)
                        .doctorId(doctorId)
                        .appointmentType(AppointmentTypes.CONSULTATION)
                        .appointmentDateTime(localDate)
                        .build()),
                eq(diagnosisId));
        doReturn(diagnosisId).when(diagnosisDao).addDiagnosis(eq(diagnosisText), eq(DiagnosisTypes.TREATING_IS_FINISHED));
        doNothing().when(appointmentDao).addDiagnosis(eq(returnedAppointmentDto.id()), eq(diagnosisId));
        doNothing().when(userDao).updatePatientTreatment(eq(patientId), eq(false));
        doNothing().when(usersDoctorsDao).deleteAllByPatientId(eq(patientId));
        assertDoesNotThrow(() -> patientService.dischargePatient(patientId, doctorId, diagnosisText,localDate));
        verify(userDao, times(1)).getPatientById(patientId);
        verify(usersDoctorsDao, times(1)).patientIsTreatedByDoctor(patientId, doctorId);
        verify(appointmentDao, times(1)).getLastAppointmentByPatientId(patientId);
        verify(diagnosisDao, times(2)).addDiagnosis(diagnosisText, DiagnosisTypes.TREATING_IS_FINISHED);
        verify(appointmentDao, times(1)).addAppointment(appointmentDto, diagnosisId);
        verify(appointmentDao, times(1)).addDiagnosis(returnedAppointmentDto.id(), diagnosisId);
        verify(userDao, times(1)).updatePatientTreatment(patientId, false);
        verify(usersDoctorsDao, times(1)).deleteAllByPatientId(patientId);
    }*/

    @Test
    public void successCheckPatientIsTreatedByDoctor() {
        doReturn(true)
                .when(usersDoctorsDao)
                .patientIsTreatedByDoctor(eq(13L), eq(24L));
        boolean actualIsTreated = patientService
                .checkPatientIsTreatedByDoctor(13L, 24L);
        assertTrue(actualIsTreated);
    }

    @Test
    public void notSuccessCheckPatientIsTreatedByDoctor() {
        doThrow(new RuntimeException("Runtime exception was thrown"))
                .when(usersDoctorsDao)
                .patientIsTreatedByDoctor(eq(14L), eq(25L));
        Throwable expectedException = assertThrows(RuntimeException.class,
                () -> patientService.checkPatientIsTreatedByDoctor(14L, 25L));
        assertEquals("Runtime exception was thrown", expectedException.getMessage());
    }
}