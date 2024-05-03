package com.oviktor.dao;

import com.oviktor.dto.AppointmentPermissionDto;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.oviktor.connection.DbConnections.getConnection;

@Slf4j
public class AppointmentPermissionDao {

    public List<AppointmentPermissionDto> getAllPermissions() {
        List<AppointmentPermissionDto> permissions = new ArrayList<>();
        String sql = """
                select r.id as role_id, r.role_name, a.id as type_id, a.type_name
                from appointments_permissions
                         join appointment_types a on a.id = appointments_permissions.appointment_type_id
                         join roles r on r.id = appointments_permissions.role_id;""";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                permissions.add(
                        AppointmentPermissionDto.builder()
                                .roleId(rs.getLong("role_id"))
                                .roleName(rs.getString("role_name"))
                                .appointmentTypeId(rs.getLong("type_id"))
                                .appointmentTypeName(rs.getString("type_name"))
                                .build()
                );
            }
            return permissions;
        } catch (SQLException e) {
            log.error("Error", e);
            throw new RuntimeException(e);
        }
    }
}
