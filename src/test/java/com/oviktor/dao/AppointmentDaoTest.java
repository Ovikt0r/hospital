package com.oviktor.dao;

import com.oviktor.connection.DbConnections;
import com.oviktor.dao.utils.Pagination;
import com.oviktor.dao.utils.Sorting;
import com.oviktor.dto.*;
import com.oviktor.entity.Appointment;



import com.oviktor.enums.DiagnosisTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import com.oviktor.enums.AppointmentTypes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class AppointmentDaoTest implements PageableDao {
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;
    DataSource dataSource;
    AppointmentDao dao;

    @BeforeEach
    public void setUp() {
        dataSource = mock(DataSource.class);
        AppointmentTypes.CONSULTATION.setId(1L);
        AppointmentTypes.MEDICATION.setId(2L);
        DiagnosisTypes.MEDITATION.setId(1L);
        DiagnosisTypes.TREATING.setId(2L);
        DiagnosisTypes.SOUND_HEALING.setId(3L);
        DbConnections.instantiateCustom(dataSource);
        dao = new AppointmentDao();
    }


    @Test
    public void addAppointmentWithDiagnosisIdTest() throws SQLException {
        doReturn(connection)
                .when(dataSource).getConnection();
        doReturn(preparedStatement)
                .when(connection).prepareStatement(any(String.class), any(Integer.class));
        doReturn(1)
                .when(preparedStatement).executeUpdate();
        doReturn(resultSet)
                .when(preparedStatement).getGeneratedKeys();
        doReturn(true)
                .when(resultSet).next();
        doReturn(1L)
                .when(resultSet).getLong(1);

        MakeAppointmentDto appointmentDto = MakeAppointmentDto.builder()
                .patientId(1L)
                .doctorId(2L)
                .appointmentType(AppointmentTypes.CONSULTATION)
                .appointmentDateTime(LocalDateTime.now())
                .build();
        Long id = dao.addAppointment(appointmentDto, 1L);
        assertEquals(1L, id);
    }

    @Test
    public void addAppointmentWithoutDiagnosisIdTest() throws SQLException {
        doReturn(connection)
                .when(dataSource).getConnection();
        doReturn(preparedStatement)
                .when(connection).prepareStatement(any(String.class), any(int.class));
        doReturn(1)
                .when(preparedStatement).executeUpdate();
        doReturn(resultSet)
                .when(preparedStatement).getGeneratedKeys();
        doReturn(true)
                .when(resultSet).next();
        doReturn(1L)
                .when(resultSet).getLong(1);


        MakeAppointmentDto appointmentDto = MakeAppointmentDto.builder()
                .patientId(1L)
                .doctorId(2L)
                .appointmentType(AppointmentTypes.MEDICATION)
                .appointmentDateTime(LocalDateTime.now())
                .build();
        Long id = dao.addAppointment(appointmentDto);
        assertEquals(1L, id);
    }

    @Test
    void getAppointmentsByPatientIdTest() throws SQLException {

        Long patientId = 1L;
        Sorting sorting = Sorting.ASC;
        Pagination pagination = Pagination.pageNum(1);
        List<AppointmentDto> appointmentDtos = new ArrayList<>();
        PagedAppointmentsDto expectedResult = PagedAppointmentsDto.builder()
                .appointments(appointmentDtos)
                .pageNum(pagination.getPageNum())
                .numOfPages(10)
                .build();

        doReturn(connection)
                .when(dataSource)
                .getConnection();
        doReturn(preparedStatement)
                .when(connection)
                .prepareStatement(any(String.class));
        doReturn(resultSet)
                .when(preparedStatement)
                .executeQuery();
        doReturn(true)
                .doReturn(false)
                .when(resultSet)
                .next();
        doReturn(100)
                .when(resultSet)
                .getInt(1);

        PagedAppointmentsDto actualResult = dao.getAppointmentsByPatientId(patientId, sorting, pagination);

        Assertions.assertNotNull(actualResult);
        assertEquals(expectedResult, actualResult);
        verify(resultSet).getInt(1);
        verify(resultSet, times(2)).next();
        verify(preparedStatement, times(2)).executeQuery();
    }

    @Test
    void updateAppointmentTest() throws SQLException {

        LocalDate localDate = LocalDate.of(2021, 5, 21);
        LocalTime localTime = LocalTime.of(15, 12);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        UpdateAppointmentDto updateAppointmentDto = new UpdateAppointmentDto(1L, AppointmentTypes.CONSULTATION, localDateTime);

        doReturn(connection)
                .when(dataSource)
                .getConnection();
        doReturn(preparedStatement)
                .when(connection)
                .prepareStatement(any(String.class));
        doNothing()
                .when(preparedStatement)
                .setLong(eq(1), eq(updateAppointmentDto.appointmentType().getId()));
        doNothing()
                .when(preparedStatement)
                .setTimestamp(eq(2), eq(Timestamp.valueOf(updateAppointmentDto.appointmentDateTime())));
        doNothing()
                .when(preparedStatement)
                .setLong(eq(3), eq(updateAppointmentDto.appointmentId()));
        doReturn(1)
                .when(preparedStatement)
                .executeUpdate();

        Assertions.assertDoesNotThrow(() -> dao.updateAppointment(updateAppointmentDto));
        verify(dataSource).getConnection();
        verify(preparedStatement, times(1)).setLong(1, updateAppointmentDto.appointmentType().getId());
        verify(preparedStatement, times(1)).setTimestamp(2, Timestamp.valueOf(updateAppointmentDto.appointmentDateTime()));
        verify(preparedStatement, times(1)).setLong(3, updateAppointmentDto.appointmentId());
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void getAppointmentByIdTest() throws SQLException {
        LocalDate localDate = LocalDate.of(2021, 5, 21);
        LocalTime localTime = LocalTime.of(15, 12);
        Long appointmentId = 1L;
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        Appointment expectedAppointment = Appointment.builder()
                .patientId(1L)
                .doctorId(1L)
                .appointmentType(AppointmentTypes.MEDICATION)
                .diagnosisId(1L)
                .appointmentDate(localDateTime)
                .canceled(false)
                .build();
        doReturn(connection)
                .when(dataSource)
                .getConnection();
        doReturn(preparedStatement)
                .when(connection)
                .prepareStatement(any(String.class));
        doNothing()
                .when(preparedStatement)
                .setLong(eq(1), eq(appointmentId));
        doReturn(resultSet)
                .when(preparedStatement)
                .executeQuery();
        doReturn(true)
                .doReturn(false)
                .when(resultSet)
                .next();
        doReturn(1L)
                .when(resultSet)
                .getLong(eq("id"));
        doReturn(1L)
                .when(resultSet)
                .getLong(eq("patient_id"));
        doReturn(1L)
                .when(resultSet)
                .getLong(eq("doctor_id"));
        doReturn(2L)
                .when(resultSet)
                .getLong(eq("appointment_type_id"));
        doReturn(1L)
                .when(resultSet)
                .getLong(eq("diagnosis_id"));
        doReturn(timestamp)
                .when(resultSet)
                .getTimestamp(eq("appointment_date"));
        doReturn(false)
                .when(resultSet)
                .getBoolean("canceled");

        Appointment actualAppointment = dao.getAppointmentById(appointmentId);
        assertEquals(expectedAppointment, actualAppointment);

    }

    @Test
    void getAppointmentWithDiagnosisByIdTest() throws SQLException {

        Long appointmentId = 100L;
        Long diagnosisId = 25L;
        DiagnosisTypes ds = DiagnosisTypes.TREATING;
        AppointmentTypes at = AppointmentTypes.CONSULTATION;
        LocalDate localDate = LocalDate.of(2021, 5, 21);
        LocalTime localTime = LocalTime.of(15, 12);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        DiagnosisDto diagnosis = DiagnosisDto.builder()
                .id(diagnosisId)
                .diagnosisType(ds)
                .text("Some text description")
                .build();
        AppointmentDto expectedAppointmentDto = AppointmentDto.builder()
                .id(appointmentId)
                .patientId(33L)
                .patientLastName("Jonson")
                .patientFirstName("Boris")
                .doctorId(22L)
                .doctorLastName("Homey")
                .doctorFirstName("Alfredo")
                .appointmentType(at)
                .diagnosis(diagnosis)
                .appointmentDateTime(localDateTime)
                .canceled(true)
                .build();

        doReturn(connection)
                .when(dataSource)
                .getConnection();
        doReturn(preparedStatement)
                .when(connection)
                .prepareStatement(any(String.class));
        doNothing()
                .when(preparedStatement)
                .setLong(eq(1), eq(appointmentId));
        doReturn(resultSet)
                .when(preparedStatement)
                .executeQuery();
        doReturn(true)
                .doReturn(false)
                .when(resultSet)
                .next();
        doReturn(appointmentId)
                .when(resultSet)
                .getLong(eq("appointment_id"));
        doReturn(33L)
                .when(resultSet)
                .getLong(eq("patient_id"));
        doReturn("Jonson")
                .when(resultSet)
                .getString(eq("patient_last_name"));
        doReturn("Boris")
                .when(resultSet)
                .getString(eq("patient_first_name"));
        doReturn(22L)
                .when(resultSet)
                .getLong(eq("doctor_id"));
        doReturn("Homey")
                .when(resultSet)
                .getString(eq("doctor_last_name"));
        doReturn("Alfredo")
                .when(resultSet)
                .getString(eq("doctor_first_name"));
        doReturn(1L)
                .when(resultSet)
                .getLong(eq("appointment_type_id"));
        doReturn("Some text description")
                .when(resultSet)
                .getString(eq("text_description"));
        doReturn(2L)
                .when(resultSet)
                .getLong(eq("diagnosis_type_id"));
        doReturn(25L)
                .when(resultSet)
                .getLong(eq("diagnosis_id"));
        doReturn(timestamp)
                .when(resultSet)
                .getTimestamp(eq("appointment_date"));
        doReturn(true)
                .when(resultSet)
                .getBoolean(eq("canceled"));

        AppointmentDto actualAppointmentDto = dao.getAppointmentWithDiagnosisById(appointmentId);
        assertEquals(expectedAppointmentDto, actualAppointmentDto);

    }

    @Test
    void cancelAppointmentTest() throws SQLException {

        long appointmentId = 1L;
        doReturn(connection)
                .when(dataSource)
                .getConnection();
        doReturn(preparedStatement)
                .when(connection)
                .prepareStatement(anyString());

        assertDoesNotThrow(()->dao.cancelAppointment(appointmentId));

        verify(preparedStatement).setBoolean(1, true);
        verify(preparedStatement).setLong(2, appointmentId);
        verify(preparedStatement).executeUpdate();

    }

    @Test
    void addDiagnosisTest() throws SQLException {

        long appointmentId = 123;
        Long diagnosisId = 456L;

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> appointmentIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> diagnosisIdCaptor = ArgumentCaptor.forClass(Long.class);

        doReturn(connection)
                .when(dataSource)
                .getConnection();
        doReturn(preparedStatement)
                .when(connection)
                .prepareStatement(sqlCaptor.capture());
        doReturn(1).when(preparedStatement).executeUpdate();
        doNothing().when(preparedStatement).close();

        assertDoesNotThrow(()->dao.addDiagnosis(appointmentId, diagnosisId));

        verify(connection, times(1)).prepareStatement(anyString());
        verify(preparedStatement, times(1)).setLong(eq(1), diagnosisIdCaptor.capture());
        verify(preparedStatement,times(1)).setLong(eq(2), appointmentIdCaptor.capture());
        verify(preparedStatement,times(1)).executeUpdate();
        verify(preparedStatement,times(1)).close();
        verifyNoMoreInteractions(connection, preparedStatement);

        assertEquals("""
                update appointments
                set diagnosis_id=?
                WHERE id=?;""", sqlCaptor.getValue());
        assertEquals(appointmentId, appointmentIdCaptor.getValue());
        assertEquals(diagnosisId, diagnosisIdCaptor.getValue());
    }
    @Test
    void getLastAppointmentByPatientIdTest(){

    }

}