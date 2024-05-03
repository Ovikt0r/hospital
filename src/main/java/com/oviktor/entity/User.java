package com.oviktor.entity;


import com.oviktor.enums.Roles;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class User implements Serializable {

    @EqualsAndHashCode.Exclude
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String usersPassword;
    private LocalDate dateOfBirth;
    private Roles role;
    private Boolean isTreated;

}
