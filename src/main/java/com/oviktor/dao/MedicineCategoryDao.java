package com.oviktor.dao;

import com.oviktor.entity.MedicineCategory;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.oviktor.connection.DbConnections.getConnection;

@Slf4j
public class MedicineCategoryDao {

    public List<MedicineCategory> getAll() {
        List<MedicineCategory> categories = new ArrayList<>();
        String sql = "SELECT id, category_name FROM medicine_categories";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                categories.add(MedicineCategory.builder()
                        .id(rs.getLong("id"))
                        .categoryName(rs.getString("category_name"))
                        .build());
            }
            return categories;
        } catch (SQLException e) {
            log.error("Error", e);
            throw new RuntimeException(e);
        }
    }
}
