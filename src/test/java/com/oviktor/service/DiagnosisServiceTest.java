package com.oviktor.service;

import com.oviktor.dao.AppointmentDao;
import com.oviktor.dao.DiagnosisDao;
import com.oviktor.dao.UsersDoctorsDao;
import com.oviktor.dto.UserInfo;
import com.oviktor.entity.Appointment;
import com.oviktor.entity.Diagnosis;
import com.oviktor.enums.AppointmentTypes;
import com.oviktor.enums.DiagnosisTypes;
import com.oviktor.service.impl.DiagnosisServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiagnosisServiceTest {


    @Mock
    private AppointmentDao appointmentDao;

    @Mock
    private DiagnosisDao diagnosisDao;

    @Mock
    private UsersDoctorsDao usersDoctorsDao;
    @Mock
    Appointment appointment;

    DiagnosisService diagnosisService;

    @BeforeEach
    void init() {
        diagnosisService = new DiagnosisServiceImpl(appointmentDao, diagnosisDao, usersDoctorsDao);
    }


    @Test
    void successDiagnoseTest() {
        String text = "Some diagnosis description";
        DiagnosisTypes diagnosisType = DiagnosisTypes.TREATING;
        UserInfo userInfo = new UserInfo(24L,"DOCTOR");
        Appointment appointment = Appointment.builder()
                .id(32L)
                .patientId(44L)
                .doctorId(24L)
                .appointmentType(AppointmentTypes.CONSULTATION)
                .appointmentDate(LocalDateTime.of(LocalDate.of(2023,6,12), LocalTime.of(13,25)))
                .diagnosisId(55L)
                .canceled(false)
                .build();
        doReturn(appointment)
                .when(appointmentDao)
                .getAppointmentById(eq(32L));
        doReturn(true).when(usersDoctorsDao)
                .patientIsTreatedByDoctor(44L,24L);
        doReturn(55L)
                .when(diagnosisDao)
                .addDiagnosis(eq(text), eq(diagnosisType), anyBoolean());

        doNothing()
                .when(appointmentDao)
                .addDiagnosis(eq(32L), eq(55L), anyBoolean());
        assertDoesNotThrow(() -> diagnosisService.diagnose(userInfo, 32L, text, diagnosisType));
    }

    @Test
    void notSuccessDiagnoseTest() {
        String text = "Some diagnosis description";
        DiagnosisTypes diagnosisType = DiagnosisTypes.TREATING;
        UserInfo userInfo1 = new UserInfo(13L,"doctor");
        doReturn(appointment)
                .when(appointmentDao)
                .getAppointmentById(32L);
        doThrow(new RuntimeException())
                .when(usersDoctorsDao).patientIsTreatedByDoctor(13L,userInfo1.id());
        assertThrows(RuntimeException.class,
                () -> diagnosisService.diagnose(userInfo1, 32L, text, diagnosisType));

    }

    @Test
    void successGetDiagnosisById() {
        Diagnosis diagnosis = Diagnosis.builder()
                .id(3L)
                .text("Text description diagnosis")
                .diagnosisType(DiagnosisTypes.OPERATION)
                .build();
        doReturn(diagnosis)
                .when(diagnosisDao)
                .getDiagnosisById(eq(3L));
        assertDoesNotThrow(() -> diagnosisService.getDiagnosisById(3L));
    }

    @Test
    void notSuccessGetDiagnosisById() {
        doThrow(new RuntimeException("The runtime exception was thrown"))
                .when(diagnosisDao)
                .getDiagnosisById(eq(6L));
        Throwable actualException = assertThrows(RuntimeException.class,
                () -> diagnosisService.getDiagnosisById(6L));
        assertEquals("The runtime exception was thrown", actualException.getMessage());
    }

    @Test
    void successUpdateDiagnosis() {
        DiagnosisTypes diagnosisTypes = DiagnosisTypes.MEDITATION;
        String text = "Some text";
        doNothing()
                .when(diagnosisDao)
                .updateDiagnosis(7L, text, diagnosisTypes);
        assertDoesNotThrow(() -> diagnosisService.updateDiagnosis(7L, text, diagnosisTypes));
    }

    @Test
    void notSuccessUpdateDiagnosis() {
        DiagnosisTypes diagnosisTypes = DiagnosisTypes.MEDITATION;
        String text = "Some text";
        doThrow(new RuntimeException("The runtime exception was thrown"))
                .when(diagnosisDao)
                .updateDiagnosis(17L, text, diagnosisTypes);
        Throwable actualException = assertThrows(RuntimeException.class,
                () -> diagnosisService.updateDiagnosis(17L, text, diagnosisTypes));
        assertEquals("The runtime exception was thrown", actualException.getMessage());
    }
}