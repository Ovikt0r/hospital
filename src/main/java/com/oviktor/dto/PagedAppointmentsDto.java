package com.oviktor.dto;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record PagedAppointmentsDto(
        List<AppointmentDto> appointments,
        int pageNum,
        int numOfPages
) implements Serializable {
}
