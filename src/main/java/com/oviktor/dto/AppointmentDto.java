package com.oviktor.dto;

import com.oviktor.enums.AppointmentTypes;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public final class AppointmentDto implements Serializable {

    private Long id;
    private Long patientId;
    private String patientLastName;
    private String patientFirstName;
    private Long doctorId;
    private String doctorLastName;
    private String doctorFirstName;
    private AppointmentTypes appointmentType;
    private DiagnosisDto diagnosis;
    private LocalDateTime appointmentDateTime;
    private Boolean canceled;

    public String getDateAndTime(){
        return appointmentDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }


}
