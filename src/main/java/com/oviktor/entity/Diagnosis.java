package com.oviktor.entity;

import com.oviktor.enums.DiagnosisTypes;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Diagnosis implements Serializable {

    @EqualsAndHashCode.Exclude
    private Long id;
    private String text;
    private DiagnosisTypes diagnosisType;

    public Diagnosis(String text, DiagnosisTypes diagnosisType) {
        this.text = text;
        this.diagnosisType = diagnosisType;
    }
}
