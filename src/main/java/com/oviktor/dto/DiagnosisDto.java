package com.oviktor.dto;

import com.oviktor.enums.DiagnosisTypes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
public record DiagnosisDto(
        long id,
        String text,
        DiagnosisTypes diagnosisType
) implements Serializable {
}
