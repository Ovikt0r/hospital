package com.oviktor.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Builder
@Data
@EqualsAndHashCode
@RequiredArgsConstructor
public final class CreateUserDto implements Serializable {
        @Serial
        private static final long serialVersionUID = 0L;
        private final String firstName;
        private final String lastName;
        private final String phone;
        private final String email;
        @EqualsAndHashCode.Exclude
        private final String usersPassword;
        private final LocalDate dateOfBirth;

}
