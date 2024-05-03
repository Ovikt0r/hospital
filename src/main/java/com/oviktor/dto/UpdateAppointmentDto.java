package com.oviktor.dto;

import com.oviktor.enums.AppointmentTypes;

import java.io.Serializable;
import java.time.LocalDateTime;

public record UpdateAppointmentDto(
        Long appointmentId,
        AppointmentTypes appointmentType,
        LocalDateTime appointmentDateTime
) implements Serializable {
}
