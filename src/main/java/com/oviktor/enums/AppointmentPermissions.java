package com.oviktor.enums;


import com.oviktor.dto.TypesDto;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum AppointmentPermissions implements Serializable {

    ADMIN_APPOINTMENT_PERMISSIONS,
    DOCTOR_APPOINTMENT_PERMISSIONS,
    NURSE_APPOINTMENT_PERMISSIONS;

    private static final Map<Roles, AppointmentPermissions> VALUES_BY_ID = new HashMap<>();

    private List<TypesDto> permissions;

    public void setPermissions(Roles role, List<TypesDto> permissions) {
        this.permissions = permissions;
        VALUES_BY_ID.put(role, this);
    }

    public static List<TypesDto> getByRole(Roles role) {
        return VALUES_BY_ID.get(role).getPermissions().stream()
                .map(type -> new TypesDto(type.id(), "appointment-type." + type.value().toLowerCase()))
                .collect(Collectors.toList());
    }
}
