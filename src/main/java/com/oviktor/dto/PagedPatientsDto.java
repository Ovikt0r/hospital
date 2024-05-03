package com.oviktor.dto;

import java.io.Serializable;
import java.util.List;

public record PagedPatientsDto(
        List<PatientDto> patients,
        int pageNum,
        int numOfPages
) implements Serializable {
}
