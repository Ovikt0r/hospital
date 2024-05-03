package com.oviktor.dao;

import com.oviktor.entity.DiagnosisType;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.oviktor.connection.DbConnections.getConnection;

@Slf4j
public class DiagnosisTypeDao {

    public List<DiagnosisType> getAll() {
        List<DiagnosisType> diagnosisTypes = new ArrayList<>();
        String sql = "SELECT id, type_name FROM diagnosis_types";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                diagnosisTypes.add(new DiagnosisType(rs.getLong("id"), rs.getString("type_name")));
            }
            return diagnosisTypes;
        } catch (SQLException e) {
            log.error("Error", e);
            throw new RuntimeException(e);
        }
    }
}
