package com.oviktor.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiagnosisType implements Serializable {
    Long id;
    String typeName;
}
