package com.oviktor.enums;

import com.oviktor.dto.TypesDto;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum MedicineCategories implements Serializable {

    PEDIATRICIAN,
    TRAUMATOLOGIST,
    SURGEON,
    NEUROLOGISTS;

    private static final Map<Long, MedicineCategories> VALUES_BY_ID = new HashMap<>();

    private Long id;

    public void setId(Long id) {
        this.id = id;
        VALUES_BY_ID.put(id, this);
    }

    public static MedicineCategories getById(Long id) {
        return VALUES_BY_ID.get(id);
    }

    public static List<TypesDto> getAll() {
        return VALUES_BY_ID.entrySet().stream()
                .map(entry -> new TypesDto(entry.getKey(), "categories." + entry.getValue().toString().toLowerCase()))
                .collect(Collectors.toList());
    }



}
