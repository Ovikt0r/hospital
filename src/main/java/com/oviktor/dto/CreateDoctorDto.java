package com.oviktor.dto;

import com.oviktor.enums.MedicineCategories;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
public record CreateDoctorDto(
        String firstName,
        String lastName,
        String phone,
        String email,
        String password,
        LocalDate birthday,
        MedicineCategories category
) implements Serializable {
}
