package com.oviktor.connection;

import com.oviktor.ApplicationContext;
import com.oviktor.dao.*;
import com.oviktor.dto.AppointmentPermissionDto;
import com.oviktor.dto.TypesDto;
import com.oviktor.entity.AppointmentType;
import com.oviktor.entity.DiagnosisType;
import com.oviktor.entity.MedicineCategory;
import com.oviktor.entity.Role;
import com.oviktor.enums.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class DataSourceInitializer {

    public static void initialize() throws ClassNotFoundException, IOException {
        DbConnections.instantiateDefault();
        initializeEnums();
        log.info("Database connection and enums was initialized");
    }

    public static void initialize(String driver, String url, String username, String password) throws ClassNotFoundException {
        DbConnections.instantiateCustom(driver, url, username, password);
    }

    public static void initializeEnums() {
        log.info("Start enums initialization");
        initializeRoles();
        initializeAppointmentPermissions();
        initializeAppointmentTypes();
        initializeMedicineCategories();
        initializeDiagnosisTypes();
        log.info("All enums was initialized successfully");

    }

    private static void initializeRoles() {
        RoleDao roleDao = new RoleDao();
        List<Role> roles = roleDao.getAll();
        for (Role role : roles) {
            String roleName = role.getRoleName();
            Roles roleEnum = Roles.valueOf(roleName);
            roleEnum.setId(role.getId());
            log.debug("Role " + roleName + "was given id " + role.getId());
        }
        log.info("Roles was initialized");
    }

    private static void initializeAppointmentPermissions() {
        AppointmentPermissionDao permissionDao = ApplicationContext.getInstance().getAppointmentPermissionDao();
        permissionDao.getAllPermissions().stream()
                .collect(Collectors.groupingBy(AppointmentPermissionDto::roleId))
                .forEach((key, value) -> {
                    AppointmentPermissions appointmentPermission = AppointmentPermissions.valueOf(
                            Roles.getById(key).toString() + "_APPOINTMENT_PERMISSIONS");
                    appointmentPermission.setPermissions(
                            Roles.getById(key),
                            value.stream()
                                    .map(dto -> new TypesDto(dto.appointmentTypeId(), dto.appointmentTypeName()))
                                    .collect(Collectors.toList())
                    );
                });
        log.info("AppointmentPermissions was initialized");
    }

    private static void initializeAppointmentTypes() {
        AppointmentTypeDao appointmentTypeDao = new AppointmentTypeDao();
        List<AppointmentType> appointmentTypes = appointmentTypeDao.getAllAppointmentTypes();
        for (AppointmentType appointmentType : appointmentTypes) {
            String typeName = appointmentType.getType();
            AppointmentTypes typeEnum = AppointmentTypes.valueOf(typeName);
            typeEnum.setId(appointmentType.getId());
            log.trace("Appointment type " + typeName + "was given id " + appointmentType.getId());
        }
        log.info("AppointmentTypes was initialized");
    }

    private static void initializeMedicineCategories() {
        MedicineCategoryDao categoryDao = new MedicineCategoryDao();
        List<MedicineCategory> categories = categoryDao.getAll();
        for (MedicineCategory category : categories) {
            String categoryName = category.getCategoryName();
            MedicineCategories categoryEnum = MedicineCategories.valueOf(categoryName);
            categoryEnum.setId(category.getId());
            log.trace("Medicine category " + categoryName + "was given id " + category.getId());
        }
        log.info("MedicineCategories was initialized");
    }

    private static void initializeDiagnosisTypes() {
        DiagnosisTypeDao diagnosisTypeDao = new DiagnosisTypeDao();
        List<DiagnosisType> diagnosisTypes = diagnosisTypeDao.getAll();
        for (DiagnosisType diagnosisType : diagnosisTypes) {
            String typeName = diagnosisType.getTypeName();
            DiagnosisTypes diagnosisTypeEnum = DiagnosisTypes.valueOf(typeName);
            diagnosisTypeEnum.setId(diagnosisType.getId());
            log.trace("Diagnosis type " + typeName + "was given id " + diagnosisType.getId());
        }
        log.info("DiagnosisTypes was initialized");
    }

}
