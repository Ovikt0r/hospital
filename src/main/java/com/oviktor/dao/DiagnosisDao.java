package com.oviktor.dao;

import com.oviktor.entity.Diagnosis;
import com.oviktor.enums.DiagnosisTypes;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

import static com.oviktor.connection.DbConnections.getConnection;
import static com.oviktor.connection.DbConnections.getCurrentThreadConnection;

@Slf4j
public class DiagnosisDao {

    public Long addDiagnosis(String text, DiagnosisTypes diagnosisTypes, boolean transactional) {
        return transactional ? addDiagnosisWithinTransaction(text, diagnosisTypes) : addDiagnosis(text, diagnosisTypes);
    }
    public Long addDiagnosisWithinTransaction(String text, DiagnosisTypes diagnosisTypes) {
        try {
            Connection connection = getCurrentThreadConnection();
            return addDiagnosis(text, diagnosisTypes, connection);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Long addDiagnosis(String text, DiagnosisTypes diagnosisTypes) {
            try {
                Connection connection = getConnection();
                return addDiagnosis(text, diagnosisTypes, connection);
            }
            catch (SQLException e) {
                log.error("****************ERROR*******************");
                throw new RuntimeException(e);
            }
    }

    private Long addDiagnosis(String text, DiagnosisTypes diagnosisTypes, Connection connection) {

        String sql = """
                insert into diagnoses(text_description, diagnosis_type_id)
                values (?, ?)
                returning id;""";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, text);
            preparedStatement.setLong(2, diagnosisTypes.getId());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new RuntimeException();
        }
        return null;
    }

    public Diagnosis getDiagnosisById(Long diagnosisId) {
        Diagnosis diagnosis = null;
        String sql = "select text_description, diagnosis_type_id from diagnoses where id=?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, diagnosisId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                diagnosis = Diagnosis.builder()
                        .id(diagnosisId)
                        .text(rs.getString("text_description"))
                        .diagnosisType(DiagnosisTypes.getById(rs.getLong("diagnosis_type_id")))
                        .build();
            }
            return diagnosis;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new RuntimeException();
        }
    }

    public void updateDiagnosis(long id, String text, DiagnosisTypes diagnosisType) {
        String sql = "update diagnoses set text_description = ?, diagnosis_type_id = ? where id=?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, text);
            preparedStatement.setLong(2, diagnosisType.getId());
            preparedStatement.setLong(3, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public void deleteAllDiagnoses() {
        String sql = "truncate table diagnoses cascade";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
