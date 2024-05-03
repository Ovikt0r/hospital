package com.oviktor.enums;

import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum Roles implements Serializable {

    DOCTOR,
    NURSE,
    PATIENT,
    ADMIN;

    private static final Map<Long, Roles> VALUES_BY_ID = new HashMap<>();

    private Long id;

    public void setId(Long id) {
        this.id = id;
        VALUES_BY_ID.put(id, this);
    }

    public static Roles getById(Long id) {
        return VALUES_BY_ID.get(id);
    }
}
