package com.oviktor.dto;

import java.io.Serializable;
import java.util.List;

public record PagedNursesDto(
        List<NurseDto> nurses,
        int pageNum,
        int numOfPages
) implements Serializable {
}
