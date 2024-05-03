package com.oviktor.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class MedicineCategory implements Serializable {
    Long id;
    String categoryName;

}
