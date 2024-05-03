package com.oviktor.dto;

import com.oviktor.enums.MedicineCategories;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DoctorDto implements Serializable
{
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private LocalDate dateOfBirth;
    private Integer numOfPatients;
    private MedicineCategories medicineCategory;

    public String getBirthDate(){
        return dateOfBirth.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
