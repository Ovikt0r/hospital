package com.oviktor.dao;

import com.oviktor.dao.utils.Pagination;
import com.oviktor.dao.utils.QueryBuilder;
import com.oviktor.dao.utils.Sorting;
import com.oviktor.dto.*;
import com.oviktor.entity.Appointment;
import com.oviktor.enums.AppointmentTypes;
import com.oviktor.enums.DiagnosisTypes;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.oviktor.connection.DbConnections.getConnection;
import static com.oviktor.connection.DbConnections.getCurrentThreadConnection;

@Slf4j
public class AppointmentDao implements PageableDao {

    public Long addAppointment(MakeAppointmentDto appointmentDto) {
        return addAppointment(appointmentDto, null);
    }

    public Long addAppointment(MakeAppointmentDto appointmentDto, Long diagnosisId) {
        String sql = """
                insert into appointments(patient_id, doctor_id, appointment_type_id, appointment_date, diagnosis_id)
                values (?, ?, ?, ?, ?)
                returning id;""";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, appointmentDto.patientId());
            preparedStatement.setLong(2, appointmentDto.doctorId());
            preparedStatement.setLong(3, appointmentDto.appointmentType().getId());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(appointmentDto.appointmentDateTime()));
            if (diagnosisId != null) {
                preparedStatement.setLong(5, diagnosisId);
            } else {
                preparedStatement.setNull(5, Types.BIGINT);
            }
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            }
            log.info("Adding an appointment to the data base was successful");
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
        return null;
    }

    public PagedAppointmentsDto getAppointmentsByPatientId(Long patientId, Sorting sorting, Pagination pagination) {
        return getAppointmentsBy(patientId, FindBy.PATIENT, sorting, pagination);
    }

    public PagedAppointmentsDto getAppointmentsByDoctorId(Long patientId, Sorting sorting, Pagination pagination) {
        return getAppointmentsBy(patientId, FindBy.DOCTOR, sorting, pagination);
    }

     PagedAppointmentsDto getAppointmentsBy(Long userId, FindBy findBy, Sorting sorting, Pagination pagination) {
        int numOfPages = calculateNumOfPages(pagination, () -> getCountAllByLong(
                """
                        select count(*)
                        from appointments
                        where
                        """ + findBy.getIdName() + " = ?;",
                userId)
        );

        List<AppointmentDto> appointments = new ArrayList<>();
        String sql = new QueryBuilder(
                """
                        select a.id         as appointment_id,
                               a.patient_id,
                               p.last_name  as patient_last_name,
                               p.first_name as patient_first_name,
                               a.doctor_id,
                               doc.last_name  as doctor_last_name,
                               doc.first_name as doctor_first_name,
                               a.appointment_type_id,
                               a.diagnosis_id,
                               a.appointment_date,
                               a.canceled,
                               d.id         as diagnosis_id,
                               d.diagnosis_type_id,
                               d.text_description
                        from appointments a
                                 full outer join diagnoses d on d.id = a.diagnosis_id
                                 inner join users doc on doc.id = a.doctor_id
                                 inner join users p on p.id = a.patient_id
                        where
                        """ + findBy.getIdName() + " = ? " +
                        "order by a.appointment_date $sorting")
                .withSorting(sorting)
                .withPagination(pagination)
                .build();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                appointments.add(
                        AppointmentDto.builder()
                                .id(rs.getLong("appointment_id"))
                                .patientId(rs.getLong("patient_id"))
                                .patientLastName(rs.getString("patient_last_name"))
                                .patientFirstName(rs.getString("patient_first_name"))
                                .doctorId(rs.getLong("doctor_id"))
                                .doctorLastName(rs.getString("doctor_last_name"))
                                .doctorFirstName(rs.getString("doctor_first_name"))
                                .appointmentType(AppointmentTypes.getById(rs.getLong("appointment_type_id")))
                                .diagnosis(
                                        rs.getLong("diagnosis_id") != 0
                                                ? new DiagnosisDto(
                                                rs.getLong("diagnosis_id"),
                                                rs.getString("text_description"),
                                                DiagnosisTypes.getById(rs.getLong("diagnosis_type_id")))
                                                : null
                                )
                                .appointmentDateTime(
                                        rs.getTimestamp("appointment_date")
                                                .toLocalDateTime()

                                )
                                .canceled(rs.getBoolean("canceled"))
                                .build()
                );
            }
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
         return new PagedAppointmentsDto(appointments,pagination.getPageNum(),numOfPages);
    }

    public void updateAppointment(UpdateAppointmentDto appointmentDto) {
        String sql = """
                update appointments
                set appointment_type_id=?,
                    appointment_date=?
                WHERE id=?;""";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, appointmentDto.appointmentType().getId());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(appointmentDto.appointmentDateTime()));
            preparedStatement.setLong(3, appointmentDto.appointmentId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public Appointment getAppointmentById(Long appointmentId) {
        Appointment appointment = null;
        String sql = """
                select id, patient_id, doctor_id, appointment_type_id, diagnosis_id, appointment_date, canceled
                from appointments
                where id=?;""";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, appointmentId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                appointment = Appointment.builder()
                        .id(rs.getLong("id"))
                        .patientId(rs.getLong("patient_id"))
                        .doctorId(rs.getLong("doctor_id"))
                        .appointmentType(AppointmentTypes.getById(rs.getLong("appointment_type_id")))
                        .diagnosisId(rs.getLong("diagnosis_id"))
                        .appointmentDate(rs.getTimestamp("appointment_date").toLocalDateTime())
                        .canceled(rs.getBoolean("canceled"))
                        .build();
            }
            if (appointment != null) {
                log.info("Retrieving data from the appointments table was successful");
            }
            return appointment;
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public AppointmentDto getAppointmentWithDiagnosisById(Long appointmentId) {
        String sql = """
                select a.id           as appointment_id,
                       a.patient_id,
                       p.last_name    as patient_last_name,
                       p.first_name   as patient_first_name,
                       a.doctor_id,
                       doc.last_name  as doctor_last_name,
                       doc.first_name as doctor_first_name,
                       a.appointment_type_id,
                       a.diagnosis_id,
                       a.appointment_date,
                       a.canceled,
                       d.id           as diagnosis_id,
                       d.diagnosis_type_id,
                       d.text_description
                from appointments a
                         full outer join diagnoses d on d.id = a.diagnosis_id
                         inner join users doc on doc.id = a.doctor_id
                         inner join users p on p.id = a.patient_id
                where a.id = ?;""";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, appointmentId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return AppointmentDto.builder()
                            .id(rs.getLong("appointment_id"))
                            .patientId(rs.getLong("patient_id"))
                            .patientLastName(rs.getString("patient_last_name"))
                            .patientFirstName(rs.getString("patient_first_name"))
                            .doctorId(rs.getLong("doctor_id"))
                            .doctorLastName(rs.getString("doctor_last_name"))
                            .doctorFirstName(rs.getString("doctor_first_name"))
                            .appointmentType(AppointmentTypes.getById(rs.getLong("appointment_type_id")))
                            .diagnosis(
                                    rs.getLong("diagnosis_id") != 0
                                            ? new DiagnosisDto(
                                            rs.getLong("diagnosis_id"),
                                            rs.getString("text_description"),
                                            DiagnosisTypes.getById(rs.getLong("diagnosis_type_id")))
                                            : null
                            )
                            .appointmentDateTime(
                                    rs.getTimestamp("appointment_date")
                                            .toLocalDateTime()

                            )
                            .canceled(rs.getBoolean("canceled"))
                            .build();
                }
                return null;
            }
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public void cancelAppointment(long appointmentId) {
        String sql = """
                update appointments
                set canceled=?
                WHERE id=?;""";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBoolean(1, true);
            preparedStatement.setLong(2, appointmentId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public void addDiagnosis(long appointmentId, Long diagnosisId, boolean transactional) {
        if (transactional) {
            addDiagnosisWithinTransaction(appointmentId, diagnosisId);
        } else {
            addDiagnosis(appointmentId, diagnosisId);
        }
    }

    private void addDiagnosisWithinTransaction(long appointmentId, Long diagnosisId) {
        try {
            Connection connection = getCurrentThreadConnection();
            addDiagnosis(appointmentId, diagnosisId, connection);
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public void addDiagnosis(long appointmentId, Long diagnosisId) {
        try {
            Connection connection = getConnection();
            addDiagnosis(appointmentId, diagnosisId, connection);
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

     void addDiagnosis(long appointmentId, Long diagnosisId, Connection connection) {
        String sql = """
                update appointments
                set diagnosis_id=?
                WHERE id=?;""";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, diagnosisId);
            preparedStatement.setLong(2, appointmentId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public AppointmentDto getLastAppointmentByPatientId(long patientId) {
        String sql = """
                select a.id           as appointment_id,
                       a.patient_id,
                       p.last_name    as patient_last_name,
                       p.first_name   as patient_first_name,
                       a.doctor_id,
                       doc.last_name  as doctor_last_name,
                       doc.first_name as doctor_first_name,
                       a.appointment_type_id,
                       a.diagnosis_id,
                       a.appointment_date,
                       a.canceled,
                       d.id           as diagnosis_id,
                       d.diagnosis_type_id,
                       d.text_description
                from appointments a
                         full outer join diagnoses d on d.id = a.diagnosis_id
                         inner join users doc on doc.id = a.doctor_id
                         inner join users p on p.id = a.patient_id
                where a.patient_id = ?
                order by a.appointment_date desc
                limit 1;""";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, patientId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return AppointmentDto.builder()
                            .id(rs.getLong("appointment_id"))
                            .patientId(rs.getLong("patient_id"))
                            .patientLastName(rs.getString("patient_last_name"))
                            .patientFirstName(rs.getString("patient_first_name"))
                            .doctorId(rs.getLong("doctor_id"))
                            .doctorLastName(rs.getString("doctor_last_name"))
                            .doctorFirstName(rs.getString("doctor_first_name"))
                            .appointmentType(AppointmentTypes.getById(rs.getLong("appointment_type_id")))
                            .diagnosis(
                                    rs.getLong("diagnosis_id") != 0
                                            ? new DiagnosisDto(
                                            rs.getLong("diagnosis_id"),
                                            rs.getString("text_description"),
                                            DiagnosisTypes.getById(rs.getLong("diagnosis_type_id")))
                                            : null
                            )
                            .appointmentDateTime(
                                    rs.getTimestamp("appointment_date")
                                            .toLocalDateTime()

                            )
                            .canceled(rs.getBoolean("canceled"))
                            .build();
                }
                return null;
            }
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public AppointmentDto getLastAppointmentByTreatedPatientId(long patientId) {
        String sql = """
                select a.id           as appointment_id,
                       a.patient_id,
                       p.last_name    as patient_last_name,
                       p.first_name   as patient_first_name,
                       a.doctor_id,
                       doc.last_name  as doctor_last_name,
                       doc.first_name as doctor_first_name,
                       a.appointment_type_id,
                       a.diagnosis_id,
                       a.appointment_date,
                       a.canceled,
                       d.id           as diagnosis_id,
                       d.diagnosis_type_id,
                       d.text_description
                from appointments a
                         full outer join diagnoses d on d.id = a.diagnosis_id
                         inner join users doc on doc.id = a.doctor_id
                         inner join users p on p.id = a.patient_id
                where a.patient_id = ?
                and d.diagnosis_type_id = ?
                order by a.appointment_date desc
                limit 1;""";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, patientId);
            preparedStatement.setLong(2,DiagnosisTypes.TREATING_IS_FINISHED.getId());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return AppointmentDto.builder()
                            .id(rs.getLong("appointment_id"))
                            .patientId(rs.getLong("patient_id"))
                            .patientLastName(rs.getString("patient_last_name"))
                            .patientFirstName(rs.getString("patient_first_name"))
                            .doctorId(rs.getLong("doctor_id"))
                            .doctorLastName(rs.getString("doctor_last_name"))
                            .doctorFirstName(rs.getString("doctor_first_name"))
                            .appointmentType(AppointmentTypes.getById(rs.getLong("appointment_type_id")))
                            .diagnosis(
                                    rs.getLong("diagnosis_id") != 0
                                            ? new DiagnosisDto(
                                            rs.getLong("diagnosis_id"),
                                            rs.getString("text_description"),
                                            DiagnosisTypes.getById(rs.getLong("diagnosis_type_id")))
                                            : null
                            )
                            .appointmentDateTime(
                                    rs.getTimestamp("appointment_date")
                                            .toLocalDateTime()

                            )
                            .canceled(rs.getBoolean("canceled"))
                            .build();
                }
                return null;
            }
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public enum FindBy {
        DOCTOR("doctor_id"),
        PATIENT("patient_id");

        @Getter
        private final String idName;

        FindBy(String idName) {
            this.idName = idName;
        }
    }
}
