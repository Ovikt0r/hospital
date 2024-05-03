package com.oviktor.dao;

import com.oviktor.dao.utils.Pagination;
import com.oviktor.dao.utils.QueryBuilder;
import com.oviktor.dao.utils.Sorting;
import com.oviktor.dto.*;
import com.oviktor.entity.User;
import com.oviktor.enums.MedicineCategories;
import com.oviktor.enums.Roles;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.oviktor.connection.DbConnections.getConnection;
import static com.oviktor.connection.DbConnections.getCurrentThreadConnection;

@Slf4j
public class UserDao implements PageableDao {

    public Long addUser(User user, boolean transactional) {
        return transactional ? addUserWithinTransaction(user) : addUser(user);
    }

    private Long addUserWithinTransaction(User user) {
        try {
            Connection connection = getCurrentThreadConnection();
            return addUser(user, connection);
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public Long addUser(User user) {
        try (Connection connection = getConnection()) {
            return addUser(user, connection);
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    private Long addUser(User user, Connection connection) {
        String sql = """
                insert into users(first_name, last_name, date_of_birth, email, phone, users_password, role_id, is_being_treated)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                returning id;""";

        try {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, user.getFirstName());
                preparedStatement.setString(2, user.getLastName());
                preparedStatement.setDate(3, Date.valueOf(user.getDateOfBirth()));
                preparedStatement.setString(4, user.getEmail());
                preparedStatement.setString(5, user.getPhone());
                preparedStatement.setString(6, user.getUsersPassword());
                preparedStatement.setLong(7, user.getRole().getId());
                preparedStatement.setBoolean(8, user.getIsTreated());
                preparedStatement.executeUpdate();
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
        return null;
    }

    public PatientDto getPatientById(Long patientId) {
        String sql = """
                select id,
                       first_name,
                       last_name,
                       date_of_birth,
                       email,
                       phone,
                       is_being_treated
                from users
                where id=?
                  and role_id = ?;""";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, patientId);
            preparedStatement.setLong(2, Roles.PATIENT.getId());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return PatientDto.builder()
                        .id(rs.getLong("id"))
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .phone(rs.getString("phone"))
                        .email(rs.getString("email"))
                        .dateOfBirth(rs.getDate("date_of_birth").toLocalDate())
                        .isTreated(rs.getBoolean("is_being_treated"))
                        .build();
            }
            return null;
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public PagedDoctorsDto getDoctorsSortedByName(Sorting sorting, MedicineCategories filterByCategory, Pagination pagination) {
        int numOfPages = calculateNumOfPages(pagination, () -> getCountAll(
                """
                        select count(*)
                        from users
                                 join doctors on users.id = doctors.user_id;""")
        );

        List<DoctorDto> doctors = new ArrayList<>();
        QueryBuilder queryBuilder = new QueryBuilder(
                """
                        select count(ud.user_id) as num_of_patients,
                               u.id,
                               u.first_name,
                               u.last_name,
                               u.date_of_birth,
                               u.email,
                               u.phone,
                               d.medicine_category_id
                        from users u
                                 join doctors d on u.id = d.user_id
                                 full outer join users_doctors ud on u.id = ud.doctor_id
                        $filtering
                        group by u.id, u.last_name, u.first_name, d.medicine_category_id
                        order by last_name $sorting, first_name $sorting""")
                .withSorting(sorting)
                .withPagination(pagination);
        if (filterByCategory != null) {
            queryBuilder = queryBuilder.withFiltering("where d.medicine_category_id = ?");
        }
        String sql = queryBuilder.build();
        log.info(sql);
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (filterByCategory != null) {
                preparedStatement.setLong(1, filterByCategory.getId());
            }
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    doctors.add(
                            DoctorDto.builder()
                                    .id(rs.getLong("id"))
                                    .firstName(rs.getString("first_name"))
                                    .lastName(rs.getString("last_name"))
                                    .phone(rs.getString("phone"))
                                    .email(rs.getString("email"))
                                    .dateOfBirth(rs.getDate("date_of_birth").toLocalDate())
                                    .numOfPatients(rs.getInt("num_of_patients"))
                                    .medicineCategory(MedicineCategories.getById(rs.getLong("medicine_category_id")))
                                    .build()
                    );
                }
            }
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
        return new PagedDoctorsDto(doctors, pagination.getPageNum(), numOfPages);
    }

    public PagedDoctorsDto getDoctorsByCategoryId(MedicineCategories medicineCategory, Sorting sorting, Pagination pagination) {
        int numOfPages = calculateNumOfPages(pagination, () -> getCountAllByLong(
                """
                        select count(*)
                        from users
                                 join doctors on users.id = doctors.user_id
                        where doctors.medicine_category_id=?""",
                medicineCategory.getId())
        );

        List<DoctorDto> doctors = new ArrayList<>();
        String sql = new QueryBuilder(
                """
                        select count(ud.user_id) as num_of_patients,
                               u.id,
                               u.first_name,
                               u.last_name,
                               u.date_of_birth,
                               u.email,
                               u.phone,
                               d.medicine_category_id
                        from users u
                                 join doctors d on u.id = d.user_id
                                 full outer join users_doctors ud on u.id = ud.doctor_id
                        where d.medicine_category_id=?
                        group by u.id, u.last_name, u.first_name, d.medicine_category_id
                        order by last_name $sorting, first_name $sorting""")
                .withSorting(sorting)
                .withPagination(pagination)
                .build();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, medicineCategory.getId());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                doctors.add(
                        DoctorDto.builder()
                                .id(rs.getLong("id"))
                                .firstName(rs.getString("first_name"))
                                .lastName(rs.getString("last_name"))
                                .phone(rs.getString("phone"))
                                .email(rs.getString("email"))
                                .dateOfBirth(rs.getDate("date_of_birth").toLocalDate())
                                .numOfPatients(rs.getInt("num_of_patients"))
                                .medicineCategory(MedicineCategories.getById(rs.getLong("medicine_category_id")))
                                .build()
                );
            }
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
        return new PagedDoctorsDto(doctors, pagination.getPageNum(), numOfPages);
    }

    public PagedDoctorsDto getDoctorsSortedByNumberOfPatients(Sorting sorting, MedicineCategories filterByCategory, Pagination pagination) {
        int numOfPages = calculateNumOfPages(pagination, () -> getCountAll(
                """
                        select count(*)
                        from users
                                 join doctors on users.id = doctors.user_id;""")
        );

        List<DoctorDto> doctors = new ArrayList<>();
        QueryBuilder queryBuilder = new QueryBuilder(
                """
                        select count(ud.user_id) as num_of_patients,
                               u.id,
                               u.first_name,
                               u.last_name,
                               u.date_of_birth,
                               u.email,
                               u.phone,
                               d.medicine_category_id
                        from users u
                                 join doctors d on u.id = d.user_id
                                 full outer join users_doctors ud on u.id = ud.doctor_id
                        $filtering
                        group by u.id, u.last_name, u.first_name, d.medicine_category_id
                        order by num_of_patients $sorting, last_name $sorting, first_name $sorting""")
                .withSorting(sorting)
                .withPagination(pagination);
        if (filterByCategory != null) {
            queryBuilder = queryBuilder.withFiltering("where d.medicine_category_id = ?");
        }
        String sql = queryBuilder.build();
        log.info(sql);
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (filterByCategory != null) {
                preparedStatement.setLong(1, filterByCategory.getId());
            }
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    doctors.add(
                            DoctorDto.builder()
                                    .id(rs.getLong("id"))
                                    .firstName(rs.getString("first_name"))
                                    .lastName(rs.getString("last_name"))
                                    .phone(rs.getString("phone"))
                                    .email(rs.getString("email"))
                                    .dateOfBirth(rs.getDate("date_of_birth").toLocalDate())
                                    .numOfPatients(rs.getInt("num_of_patients"))
                                    .medicineCategory(MedicineCategories.getById(rs.getLong("medicine_category_id")))
                                    .build()
                    );
                }
            }
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
        return new PagedDoctorsDto(doctors, pagination.getPageNum(), numOfPages);
    }

    public PagedNursesDto getNursesSortedByName(Sorting sorting, Pagination pagination) {
        int numOfPages = calculateNumOfPages(pagination, () -> getCountAllByLong(
                """
                        select count(*)
                        from users
                        where role_id=?""",
                Roles.NURSE.getId())
        );

        List<NurseDto> nurses = new ArrayList<>();
        String sql = new QueryBuilder(
                """
                        select u.id,
                               u.first_name,
                               u.last_name,
                               u.date_of_birth,
                               u.email,
                               u.phone
                        from users u
                        where u.role_id = ?
                        order by u.last_name $sorting, u.first_name $sorting""")
                .withSorting(sorting)
                .withPagination(pagination)
                .build();
        log.info(sql);
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, Roles.NURSE.getId());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    nurses.add(
                            NurseDto.builder()
                                    .id(rs.getLong("id"))
                                    .firstName(rs.getString("first_name"))
                                    .lastName(rs.getString("last_name"))
                                    .phone(rs.getString("phone"))
                                    .email(rs.getString("email"))
                                    .dateOfBirth(rs.getDate("date_of_birth").toLocalDate())
                                    .build()
                    );
                }
            }
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
        return new PagedNursesDto(nurses, pagination.getPageNum(), numOfPages);
    }

    public PagedPatientsDto getPatientsSortedByName(Sorting sorting, Pagination pagination) {
        return getPatientsSortedBy("""
                    select id,
                           first_name,
                           last_name,
                           date_of_birth,
                           email,
                           phone,
                           is_being_treated
                    from users
                    where role_id = ?
                    order by last_name $sorting, first_name $sorting""",
                sorting, pagination);
    }

    public PagedPatientsDto getPatientsSortedByDateOfBirth(Sorting sorting, Pagination pagination){
        return getPatientsSortedBy("""
                        select id,
                               first_name,
                               last_name,
                               date_of_birth,
                               email,
                               phone,
                               is_being_treated
                        from users
                        where role_id = ?
                        order by date_of_birth $sorting""",
                sorting, pagination);
    }

    private PagedPatientsDto getPatientsSortedBy(String sql, Sorting sorting, Pagination pagination)  {
        int numOfPages = calculateNumOfPages(pagination, () -> getCountAllByLong(
                """
                        select count(*)
                        from users where role_id = ?""",
                Roles.PATIENT.getId())
        );

        List<PatientDto> patients = new ArrayList<>();
        sql = new QueryBuilder(sql)
                .withSorting(sorting)
                .withPagination(pagination)
                .build();
        log.info(sql);
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, Roles.PATIENT.getId());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    patients.add(
                            PatientDto.builder()
                                    .id(rs.getLong("id"))
                                    .firstName(rs.getString("first_name"))
                                    .lastName(rs.getString("last_name"))
                                    .phone(rs.getString("phone"))
                                    .email(rs.getString("email"))
                                    .dateOfBirth(rs.getDate("date_of_birth").toLocalDate())
                                    .isTreated(rs.getBoolean("is_being_treated"))
                                    .build()
                    );
                }

            } catch (SQLException e) {
                log.error("Error", e);
                throw new RuntimeException(e);
            }
            return new PagedPatientsDto(patients, pagination.getPageNum(), numOfPages);
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public PagedPatientsDto getPatientsByDoctorIdSortedByName(long doctorId, Sorting sorting, Pagination pagination) {
        return getPatientsByDoctorIdSortedBy("""
                        select u.id,
                               u.first_name,
                               u.last_name,
                               u.date_of_birth,
                               u.email,
                               u.phone,
                               u.is_being_treated
                        from users u
                            join users_doctors ud on u.id = ud.user_id
                        where ud.doctor_id = ?
                        order by u.last_name $sorting, u.first_name $sorting""",
                doctorId, sorting, pagination);
    }

    public PagedPatientsDto getPatientsByDoctorIdSortedByDateOfBirth(long doctorId, Sorting sorting, Pagination pagination) {
        return getPatientsByDoctorIdSortedBy("""
                        select u.id,
                               u.first_name,
                               u.last_name,
                               u.date_of_birth,
                               u.email,
                               u.phone,
                               u.is_being_treated
                        from users u
                            join users_doctors ud on u.id = ud.user_id
                        where ud.doctor_id = ?
                        order by u.date_of_birth $sorting""",
                doctorId, sorting, pagination);
    }

    private PagedPatientsDto getPatientsByDoctorIdSortedBy(String sql, long doctorId, Sorting sorting, Pagination pagination) {
        int numOfPages = calculateNumOfPages(pagination, () -> getCountAllByLong(
                """
                        select count(*)
                        from users u
                                 join users_doctors ud on u.id = ud.user_id
                        where ud.doctor_id = ?;""",
                doctorId)
        );

        List<PatientDto> patients = new ArrayList<>();
        sql = new QueryBuilder(sql)
                .withSorting(sorting)
                .withPagination(pagination)
                .build();
        log.info(sql);
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, doctorId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    patients.add(
                            PatientDto.builder()
                                    .id(rs.getLong("id"))
                                    .firstName(rs.getString("first_name"))
                                    .lastName(rs.getString("last_name"))
                                    .phone(rs.getString("phone"))
                                    .email(rs.getString("email"))
                                    .dateOfBirth(rs.getDate("date_of_birth").toLocalDate())
                                    .isTreated(rs.getBoolean("is_being_treated"))
                                    .build()
                    );
                }
            }
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
        return new PagedPatientsDto(patients, pagination.getPageNum(), numOfPages);
    }

    public void deleteAllUsers() {
        String sql = "delete from users";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public DoctorDto getDoctorById(Long doctorId) {
        String sql = """
                select count(ud.user_id) as num_of_patients,
                       u.id,
                       u.first_name,
                       u.last_name,
                       u.date_of_birth,
                       u.email,
                       u.phone,
                       d.medicine_category_id
                from users u
                         join doctors d on u.id = d.user_id
                         full outer join users_doctors ud on u.id = ud.doctor_id
                where u.id = ?
                group by u.id, u.last_name, u.first_name, d.medicine_category_id;""";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, doctorId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return DoctorDto.builder()
                        .id(rs.getLong("id"))
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .phone(rs.getString("phone"))
                        .email(rs.getString("email"))
                        .dateOfBirth(rs.getDate("date_of_birth").toLocalDate())
                        .numOfPatients(rs.getInt("num_of_patients"))
                        .medicineCategory(MedicineCategories.getById(rs.getLong("medicine_category_id")))
                        .build();
            }
            return null;
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }

    }

    public NurseDto getNurseById(Long nurseId) {
        String sql = """
                select id,
                       first_name,
                       last_name,
                       date_of_birth,
                       email,
                       phone
                from users
                where id=?
                  and role_id = ?;""";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, nurseId);
            preparedStatement.setLong(2, Roles.NURSE.getId());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return NurseDto.builder()
                        .id(rs.getLong("id"))
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .phone(rs.getString("phone"))
                        .email(rs.getString("email"))
                        .dateOfBirth(rs.getDate("date_of_birth").toLocalDate())
                        .build();
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public AdminDto getAdminById(Long adminId) {
        String sql = """
                select id,
                       first_name,
                       last_name,
                       date_of_birth,
                       email,
                       phone
                from users
                where id=?
                  and role_id = ?;""";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, adminId);
            preparedStatement.setLong(2, Roles.ADMIN.getId());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return AdminDto.builder()
                        .id(rs.getLong("id"))
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .phone(rs.getString("phone"))
                        .email(rs.getString("email"))
                        .dateOfBirth(rs.getDate("date_of_birth").toLocalDate())
                        .build();
            }
            return null;
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public void updatePatientTreatment(Long patientId, boolean isTreated) {
        String sql = """
                update users
                set is_being_treated = ?
                
                where id = ?
                  and role_id = ?;""";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setBoolean(1, isTreated);
            preparedStatement.setLong(2, patientId);
            preparedStatement.setLong(3, Roles.PATIENT.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }

    public List<DoctorCompactedDto> getDoctorsByPatientId(Long patientId) {
        String sql = """
                select u.id,
                       u.first_name,
                       u.last_name,
                       d.medicine_category_id
                from users u
                         join doctors d on u.id = d.user_id
                         full outer join users_doctors ud on u.id = ud.doctor_id
                where ud.user_id = ?
                order by last_name, first_name;""";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, patientId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                List<DoctorCompactedDto> doctors = new ArrayList<>();
                while (rs.next()) {
                    doctors.add(
                            DoctorCompactedDto.builder()
                                    .id(rs.getLong("id"))
                                    .firstName(rs.getString("first_name"))
                                    .lastName(rs.getString("last_name"))
                                    .medicineCategory(MedicineCategories.getById(rs.getLong("medicine_category_id")))
                                    .build()
                    );
                }
                return doctors;
            }
        } catch (SQLException e) {
            log.error("SQLException was caught!", e);
            throw new RuntimeException();
        }
    }
}

