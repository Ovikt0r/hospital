package com.oviktor.dao;

import com.oviktor.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.oviktor.connection.DbConnections.getConnection;
import static com.oviktor.connection.DbConnections.getCurrentThreadConnection;

@Slf4j
public class UsersDoctorsDao {

    public void addPatientToDoctor(Long patientId, Long doctorId, boolean transactional){
        if (transactional) {
            addPatientToDoctorWithinTransaction(patientId, doctorId);
        } else {
            addPatientToDoctor(patientId, doctorId);
        }
    }

    @Transactional
    private void addPatientToDoctorWithinTransaction(Long patientId, Long doctorId) {
        try {
            Connection connection = getCurrentThreadConnection();
            addPatientToDoctor(patientId, doctorId, connection);
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public void addPatientToDoctor(Long patientId, Long doctorId) {
        try {
            Connection connection = getConnection();
            addPatientToDoctor(patientId, doctorId, connection);
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    private void addPatientToDoctor(Long patientId, Long doctorId, Connection connection) {
        String sql = "insert into users_doctors (user_id, doctor_id) values (?, ?);";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, patientId);
            preparedStatement.setLong(2, doctorId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }
    

    public void deleteAllByPatientId(long patientId) {
        String sql = """
                delete from users_doctors
                where user_id = ?;""";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, patientId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public boolean patientIsTreatedByDoctor(Long patientId, Long doctorId) {
        String sql = "select exists(select * from users_doctors where user_id = ? and doctor_id = ?);";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, patientId);
            preparedStatement.setLong(2, doctorId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
        return false;
    }
}
