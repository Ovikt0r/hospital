package com.oviktor.dto;

import com.oviktor.enums.AppointmentTypes;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
public record MakeAppointmentDto(
        Long patientId,
        Long doctorId,
        AppointmentTypes appointmentType,
        LocalDateTime appointmentDateTime
) implements Serializable {
}
