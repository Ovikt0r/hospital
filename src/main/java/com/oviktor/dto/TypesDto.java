package com.oviktor.dto;

import java.io.Serializable;

public record TypesDto(
        long id,
        String value
) implements Serializable {
}
