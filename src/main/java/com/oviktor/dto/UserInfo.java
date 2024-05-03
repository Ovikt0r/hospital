package com.oviktor.dto;

import java.io.Serializable;

public record UserInfo(
        Long id,
        String role
) implements Serializable {
}
