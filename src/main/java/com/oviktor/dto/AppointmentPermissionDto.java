package com.oviktor.dto;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record AppointmentPermissionDto(
        Long roleId,
        String roleName,
        Long appointmentTypeId,
        String appointmentTypeName
) implements Serializable {
}
