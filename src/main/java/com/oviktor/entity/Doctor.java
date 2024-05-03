package com.oviktor.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Getter
@Setter
@Builder
public class Doctor implements Serializable {
    Long id;
    Long userId;
    Long medicineCategoryId;
}
