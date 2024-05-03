package com.oviktor.dao;


import com.oviktor.entity.Doctor;
import com.oviktor.entity.MedicineCategory;
import com.oviktor.enums.MedicineCategories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.oviktor.connection.DbConnections.getConnection;
import static com.oviktor.connection.DbConnections.getCurrentThreadConnection;


public class DoctorDao {

    public void addDoctor(Long userId, MedicineCategories category, boolean transactional) {
        if (transactional) {
            addDoctorWithinTransaction(userId, category);
        } else {
            addDoctor(userId, category);
        }
    }

    private void addDoctorWithinTransaction(Long userId, MedicineCategories category) {
        try {
            Connection connection = getCurrentThreadConnection();
            addDoctor(userId, category.getId(), connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addDoctor(Long userId, MedicineCategories category) {
        try {
            Connection connection = getConnection();
            addDoctor(userId, category.getId(), connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void addDoctor(Long userId, Long categoryId, Connection connection) {
        String sql = "INSERT INTO doctors(user_id, medicine_category_id) VALUES (?,?)";

        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setLong(1, userId);
                preparedStatement.setLong(2, categoryId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Doctor> getSortedListOfDoctorsByNumberOfPatients() throws SQLException {
        String sql = "SELECT ID,phone,EMAIL,FIRST_NAME,LASTNAME,CATEGORY,NUMBER_OF_PATIENTS FROM DOCTOR ORDER BY NUMBER_OF_PATIENTS";
        return getDoctors(new ArrayList<>(), sql);
    }

    public List<Doctor> getAllDoctorsByCategoryId(MedicineCategory medicineCategory) throws SQLException {
        List<Doctor> doctorList = new ArrayList<>();
        String sql = "SELECT FROM doctors WHERE medicine_category_id=?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, medicineCategory.getId());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                doctorList.add(Doctor.builder()
                        .id(rs.getLong("id"))
                        .userId(rs.getLong("user_id"))
                        .build());
            }
            return doctorList;
        }
    }

    private List<Doctor> getDoctors(List<Doctor> doctorList, String sql) throws SQLException {
        return doctorList;
    }

    public void updateDoctor(Doctor doctor) throws SQLException {


    }

    public void deleteAllDoctors() {
        String sql = "DELETE  FROM doctors";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
