package com.oviktor.enums;

import com.oviktor.dto.TypesDto;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum AppointmentTypes implements Serializable {

    CONSULTATION,
    OPERATION,
    PROCEDURE,
    MEDICATION;

    private static final Map<Long, AppointmentTypes> VALUES_BY_ID = new HashMap<>();

    private Long id;

    public void setId(Long id) {
        this.id = id;
        VALUES_BY_ID.put(id, this);
    }

    public static AppointmentTypes getById(Long id) {
        return VALUES_BY_ID.get(id);
    }

    public static List<TypesDto> getAll() {
        return VALUES_BY_ID.entrySet().stream()
                .map((entry -> new TypesDto(entry.getKey(), entry.getValue().toString())))
                .collect(Collectors.toList());
    }
}
