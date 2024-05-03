package com.oviktor.dao;

import com.oviktor.entity.AppointmentType;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.oviktor.connection.DbConnections.getConnection;

@Slf4j
public class AppointmentTypeDao {

    public List<AppointmentType> getAllAppointmentTypes() {
        List<AppointmentType> appointmentTypeList = new ArrayList<>();
        String sql = "SELECT id, type_name FROM appointment_types";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                appointmentTypeList.add((AppointmentType.builder()
                        .id(rs.getLong("id"))
                        .type(rs.getString("type_name"))
                        .build()));
            }
            return appointmentTypeList;
        } catch (SQLException e) {
            log.error("Error", e);
            throw new RuntimeException(e);
        }
    }
}
