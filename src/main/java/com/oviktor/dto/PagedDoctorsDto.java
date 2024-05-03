package com.oviktor.dto;

import java.io.Serializable;
import java.util.List;

public record PagedDoctorsDto(
        List<DoctorDto> doctors,
        int pageNum,
        int numOfPages
) implements Serializable {
}
