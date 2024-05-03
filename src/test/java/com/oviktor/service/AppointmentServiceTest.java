package com.oviktor.service;

import com.oviktor.dao.AppointmentDao;
import com.oviktor.dao.AppointmentTypeDao;
import com.oviktor.dao.DiagnosisDao;
import com.oviktor.dao.UsersDoctorsDao;
import com.oviktor.dao.utils.Pagination;
import com.oviktor.dao.utils.Sorting;
import com.oviktor.dto.*;
import com.oviktor.entity.Appointment;
import com.oviktor.entity.AppointmentType;
import com.oviktor.enums.AppointmentPermissions;
import com.oviktor.enums.AppointmentTypes;
import com.oviktor.enums.DiagnosisTypes;
import com.oviktor.enums.Roles;
import com.oviktor.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentDao appointmentDao;

    @Mock
    private UsersDoctorsDao usersDoctorsDao;

    private AppointmentService appointmentService;
    @Mock
    private List<AppointmentDto> appointmentDtoList;


    @BeforeEach
    void init() {
        AppointmentTypes.CONSULTATION.setId(11L);
        AppointmentTypes.OPERATION.setId(2L);
        AppointmentTypes.PROCEDURE.setId(14L);
        AppointmentTypes.MEDICATION.setId(4L);
        AppointmentPermissions appointmentPermissions = AppointmentPermissions.DOCTOR_APPOINTMENT_PERMISSIONS;
        List<TypesDto> permissionsAdmin = Arrays.asList(
                new TypesDto(11L,"ADMIN_APPOINTMENT_PERMISSIONS"),
                new TypesDto(23L,"ADMIN_APPOINTMENT_PERMISSIONS"),
                new TypesDto(14L,"ADMIN_APPOINTMENT_PERMISSIONS")
        );
        List<TypesDto> permissionsDoctor = Arrays.asList(
                new TypesDto(111L,"DOCTOR_APPOINTMENT_PERMISSIONS"),
                new TypesDto(42L,"DOCTOR_APPOINTMENT_PERMISSIONS"),
                new TypesDto(14L,"DOCTOR_APPOINTMENT_PERMISSIONS")
        );
        List<TypesDto> permissionsNurse = Arrays.asList(
                new TypesDto(21L,"NURSE_APPOINTMENT_PERMISSIONS"),
                new TypesDto(72L,"NURSE_APPOINTMENT_PERMISSIONS"),
                new TypesDto(53L,"NURSE_APPOINTMENT_PERMISSIONS")
        );
     //   appointmentPermissions.setPermissions(Roles.ADMIN,permissionsAdmin);
        appointmentPermissions.setPermissions(Roles.DOCTOR,permissionsDoctor);
        appointmentPermissions.setPermissions(Roles.ADMIN,permissionsAdmin);
      //  appointmentPermissions.setPermissions(Roles.NURSE,permissionsNurse);
        appointmentService = new AppointmentServiceImpl(appointmentDao, usersDoctorsDao);
    }

    @Test
    void successMakeAppointment() {
        AppointmentTypes appointmentType = AppointmentTypes.PROCEDURE;
        UserInfo userInfo = new UserInfo(14L,"DOCTOR");

        MakeAppointmentDto makeAppointmentDto = MakeAppointmentDto.builder()
                .doctorId(14L)
                .patientId(6L)
                .appointmentType(appointmentType)
                .appointmentDateTime(LocalDateTime.of(LocalDate.of(2023, 8, 16), LocalTime.of(15, 23)))
                .build();

        doReturn(true).when(usersDoctorsDao).patientIsTreatedByDoctor(6L,14L);
        doReturn(1L).when(appointmentDao).addAppointment(eq(makeAppointmentDto));
        assertDoesNotThrow(() -> appointmentService.makeAppointment(userInfo, makeAppointmentDto));
        verify(appointmentDao, times(1)).addAppointment(makeAppointmentDto);

    }

    @Test
    void notSuccessMakeAppointment() {
        AppointmentTypes appointmentType = AppointmentTypes.PROCEDURE;
        UserInfo userInfo1 = new UserInfo(25L,"DOCTOR");

        MakeAppointmentDto makeAppointmentDto = MakeAppointmentDto.builder()
                .doctorId(3L)
                .patientId(6L)
                .appointmentType(appointmentType)
                .appointmentDateTime(LocalDateTime.of(LocalDate.of(2023, 8, 16), LocalTime.of(15, 23)))
                .build();
        doThrow(new RuntimeException("The runtime exception was thrown"))
                .when(appointmentDao)
                .addAppointment(makeAppointmentDto);
        Throwable actualException = assertThrows(RuntimeException.class, () -> appointmentService.makeAppointment(userInfo1, makeAppointmentDto));
       assertEquals("The runtime exception was thrown", actualException.getMessage());

    }

    @Test
    void successUpdateAppointment() {
        AppointmentTypes appointmentType = AppointmentTypes.CONSULTATION;
        UserInfo userInfo1 = new UserInfo(169L, "ADMIN");

        LocalDateTime localDateTime = LocalDateTime.of(
                LocalDate.of(2023, 12, 20),
                LocalTime.of(10, 35));
        UpdateAppointmentDto updateAppointmentDto =
                new UpdateAppointmentDto(25L, appointmentType, localDateTime);
        doNothing()
                .when(appointmentDao)
                .updateAppointment(updateAppointmentDto);
        assertDoesNotThrow(() -> appointmentService.updateAppointment(userInfo1, updateAppointmentDto));
        verify(appointmentDao, times(1)).updateAppointment(eq(updateAppointmentDto));
    }

    @Test
    void notSuccessUpdateAppointment() {

        AppointmentTypes appointmentType = AppointmentTypes.CONSULTATION;
        UserInfo userInfo1 = new UserInfo(169L, "ADMIN");
        LocalDateTime localDateTime = LocalDateTime.of(
                LocalDate.of(2023, 12, 20),
                LocalTime.of(10, 35));
        UpdateAppointmentDto appointmentDto =
                new UpdateAppointmentDto(25L, appointmentType, localDateTime);
        doThrow(new RuntimeException("The runtime exception was thrown"))
                .when(appointmentDao)
                .updateAppointment(appointmentDto);
        Throwable actualException = assertThrows(RuntimeException.class, () -> appointmentService.updateAppointment(userInfo1, appointmentDto));
        assertEquals("The runtime exception was thrown", actualException.getMessage());
        verify(appointmentDao, times(1)).updateAppointment(eq(appointmentDto));
    }

    @Test
    void successCancelAppointment() {
        doNothing().when(appointmentDao).cancelAppointment(eq(43L));
        assertDoesNotThrow(() -> appointmentService.cancelAppointment(43L));
        verify(appointmentDao, times(1)).cancelAppointment(43L);

    }

    @Test
    void notSuccessCancelAppointment() {
        doThrow(new RuntimeException("The runtime exception was thrown"))
                .when(appointmentDao)
                .cancelAppointment(eq(76L));
        Throwable actualException = assertThrows(RuntimeException.class,
                () -> appointmentService.cancelAppointment(76L));
        assertEquals("The runtime exception was thrown", actualException.getMessage());
        verify(appointmentDao, times(1)).cancelAppointment(eq(76L));
    }

    @Test
    void successGetAppointmentsByPatientId() {
        PagedAppointmentsDto pagedAppointmentsDto = new PagedAppointmentsDto(appointmentDtoList, 65, 100);
        Sorting sorting = Sorting.ASC;
        Pagination pagination = Pagination.pageNum(65);
        doReturn(pagedAppointmentsDto)
                .when(appointmentDao)
                .getAppointmentsByPatientId(eq(54L), eq(sorting), eq(pagination));
        assertDoesNotThrow(() -> appointmentService.getAppointmentsByPatientId(54L, sorting, pagination));
        verify(appointmentDao, times(1))
                .getAppointmentsByPatientId(eq(54L), eq(sorting), eq(pagination));
    }

    @Test
    void notSuccessGetAppointmentsByPatientId() {
        Sorting sorting = Sorting.ASC;
        Pagination pagination = Pagination.pageNum(65);
        doThrow(new RuntimeException("The runtime exception was thrown"))
                .when(appointmentDao)
                .getAppointmentsByPatientId(eq(54L), eq(sorting), eq(pagination));
        Throwable actualException = assertThrows(RuntimeException.class,
                () -> appointmentService.getAppointmentsByPatientId(54L, sorting, pagination));
        assertEquals("The runtime exception was thrown", actualException.getMessage());
        verify(appointmentDao, times(1))
                .getAppointmentsByPatientId(eq(54L), eq(sorting), eq(pagination));
    }

    @Test
    void successGetAppointmentsByDoctorId() {
        PagedAppointmentsDto pagedAppointmentsDto = new PagedAppointmentsDto(appointmentDtoList, 45, 46);
        Sorting sorting = Sorting.DESC;
        Pagination pagination = Pagination.pageNum(45);
        doReturn(pagedAppointmentsDto)
                .when(appointmentDao)
                .getAppointmentsByDoctorId(eq(94L), eq(sorting), eq(pagination));
        assertDoesNotThrow(() -> appointmentService.getAppointmentsByDoctorId(94L, sorting, pagination));
        verify(appointmentDao, times(1))
                .getAppointmentsByDoctorId(eq(94L), eq(sorting), eq(pagination));
    }

    @Test
    void notSuccessGetAppointmentsByDoctorId() {
        Sorting sorting = Sorting.DESC;
        Pagination pagination = Pagination.pageNum(65);
        doThrow(new RuntimeException("The runtime exception was thrown"))
                .when(appointmentDao)
                .getAppointmentsByDoctorId(eq(18L), eq(sorting), eq(pagination));
        Throwable actualException = assertThrows(RuntimeException.class,
                () -> appointmentService.getAppointmentsByDoctorId(18L, sorting, pagination));
        assertEquals("The runtime exception was thrown", actualException.getMessage());
        verify(appointmentDao, times(1))
                .getAppointmentsByDoctorId(eq(18L), eq(sorting), eq(pagination));

    }

    @Test
    void successGetAppointmentWithDiagnosisById() {
        DiagnosisDto diagnosisDto = new DiagnosisDto(12L, "", DiagnosisTypes.OPERATION);
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(2022, 8, 31), LocalTime.of(14, 18));
        AppointmentTypes appointmentType = AppointmentTypes.MEDICATION;
        AppointmentDto appointmentDto = AppointmentDto.builder()
                .id(21L)
                .patientFirstName("Leroy")
                .patientLastName("Tronhill")
                .doctorFirstName("Keyt")
                .doctorLastName("Flint")
                .appointmentDateTime(localDateTime)
                .diagnosis(diagnosisDto)
                .canceled(false)
                .patientId(432L)
                .appointmentType(appointmentType)
                .doctorId(333L)
                .build();
        doReturn(appointmentDto)
                .when(appointmentDao)
                .getAppointmentWithDiagnosisById(21L);
        assertDoesNotThrow(() -> appointmentService.getAppointmentWithDiagnosisById(21L));
        verify(appointmentDao,times(1)).getAppointmentWithDiagnosisById(21L);
    }

    @Test
    void notSuccessGetAppointmentWithDiagnosisById() {
        doThrow(new RuntimeException("The runtime exception was thrown"))
                .when(appointmentDao)
                .getAppointmentWithDiagnosisById(21L);

        Throwable actualException = assertThrows(RuntimeException.class,
                () -> appointmentService.getAppointmentWithDiagnosisById(21L));
        assertEquals("The runtime exception was thrown", actualException.getMessage());
    }

    @Test
    void successGetAppointmentById() {
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.of(2022, 8, 31), LocalTime.of(14, 18));
        Appointment appointment = Appointment.builder()
                .id(25L)
                .patientId(13L)
                .doctorId(29L)
                .diagnosisId(135L)
                .appointmentType(AppointmentTypes.PROCEDURE)
                .appointmentDate(localDateTime)
                .canceled(false)
                .build();
        doReturn(appointment).when(appointmentDao).getAppointmentById(25L);
        assertDoesNotThrow(() -> appointmentService.getAppointmentById(25L));
    }

    @Test
    void notSuccessGetAppointmentById() {
        doThrow(new RuntimeException("The runtime exception was thrown"))
                .when(appointmentDao)
                .getAppointmentById(25L);
        Throwable actualException = assertThrows(RuntimeException.class,
                () -> appointmentService.getAppointmentById(25L));
        assertEquals("The runtime exception was thrown", actualException.getMessage());
    }
}