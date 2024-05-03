package com.oviktor.entity;

import com.oviktor.enums.AppointmentTypes;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Appointment implements Serializable {

    @EqualsAndHashCode.Exclude
    private Long id;
    private Long patientId;
    private Long doctorId;
    private AppointmentTypes appointmentType;
    private Long diagnosisId;
    private LocalDateTime appointmentDate;
    private Boolean canceled;
}
