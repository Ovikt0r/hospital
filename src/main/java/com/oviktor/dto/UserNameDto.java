package com.oviktor.dto;

import java.io.Serializable;

public record UserNameDto(
        String lastName,
        String firstName
) implements Serializable {
}
