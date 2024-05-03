package com.oviktor.dto;

import com.oviktor.enums.DiagnosisTypes;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record DischargePatientDto(
        Long patientId,
        Long appointmentId,
        String diagnosisText,
        DiagnosisTypes diagnosisType,
        boolean createNewAppointment
) implements Serializable {
}
