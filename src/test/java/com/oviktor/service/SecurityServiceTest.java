package com.oviktor.service;

import com.oviktor.dao.SecurityDao;
import com.oviktor.dto.UserInfo;
import com.oviktor.enums.Roles;
import com.oviktor.service.impl.SecurityServiceImpl;
import com.password4j.Password;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceTest {

    @Mock
    private SecurityDao securityDao;

    @BeforeAll
    static void setUp() {
        Roles.ADMIN.setId(1L);
        Roles.PATIENT.setId(2L);
    }

    @Test
    void checkCredentialsAndReturnRoleSuccessTest() {
        doReturn(Optional.of(
                Password.hash("admin")
                        .withBcrypt()
                        .getResult())
        ).when(securityDao).getPasswordByLogin(eq("admin@admin.com"), anyBoolean());
        doReturn(Optional.of(new UserInfo(1L, "ADMIN")))
                .when(securityDao).getUserInfoByUserLogin(eq("admin@admin.com"), anyBoolean());

        doReturn(Optional.of(
                Password.hash("patient")
                        .withBcrypt()
                        .getResult())
        ).when(securityDao).getPasswordByLogin(eq("patient@patient.com"), anyBoolean());
        doReturn(Optional.of(new UserInfo(2L, "PATIENT")))
                .when(securityDao).getUserInfoByUserLogin(eq("patient@patient.com"), anyBoolean());

        SecurityService service = new SecurityServiceImpl(securityDao);

        Roles expectedAdminRole = Roles.ADMIN;
        Roles expectedPatientRole = Roles.PATIENT;
        Optional<UserInfo> actualAdminUserInfo = service.checkCredentialsAndReturnUserInfo("admin@admin.com", "admin");
        Optional<UserInfo> actualPatientUserInfo = service.checkCredentialsAndReturnUserInfo("patient@patient.com", "patient");

        assertAll(() -> {
            assertTrue(actualAdminUserInfo.isPresent());
            assertEquals(expectedAdminRole, Roles.valueOf(actualAdminUserInfo.get().role().toUpperCase()));
            assertTrue(actualPatientUserInfo.isPresent());
            assertEquals(expectedPatientRole, Roles.valueOf(actualPatientUserInfo.get().role().toUpperCase()));
        });
    }

    @Test
    void checkCredentialsAndReturnRoleWrongPasswordTest() {
        doReturn(Optional.of(
                Password.hash("admin")
                        .withBcrypt()
                        .getResult())
        ).when(securityDao).getPasswordByLogin(eq("admin"), anyBoolean());

        SecurityService service = new SecurityServiceImpl(securityDao);

        Optional<UserInfo> actualAdminRole = service.checkCredentialsAndReturnUserInfo("admin", "wrong-admin");

        assertTrue(actualAdminRole.isEmpty());
    }
}
