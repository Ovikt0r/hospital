package com.oviktor.dto;

import com.oviktor.enums.MedicineCategories;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DoctorCompactedDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private MedicineCategories medicineCategory;
}
