package com.oviktor.service.impl;

import com.oviktor.annotation.Transactional;
import com.oviktor.dao.SecurityDao;
import com.oviktor.dto.UserInfo;
import com.oviktor.dto.UserNameDto;
import com.oviktor.service.SecurityService;
import com.password4j.Password;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static com.oviktor.utils.RegexEmailValidator.isValidEmailAddress;

@Slf4j
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final SecurityDao securityDao;

    @Transactional
    public Optional<UserInfo> checkCredentialsAndReturnUserInfo(String login, String password) {
        Optional<String> passwordFromDbOpt = securityDao.getPasswordByLogin(login, true);
        if (passwordFromDbOpt.isEmpty()) {
            log.warn("In the process of checking credentials the password was empty!");
            return Optional.empty();
        }

        boolean verified = Password.check(password, passwordFromDbOpt.get()).withBcrypt();
        if (!verified) {
            log.warn("The password does not match!");
            return Optional.empty();
        }
        if(!isValidEmailAddress(login)){
            log.warn("The login form is not valid.It must match the email!");
            return Optional.empty();
        }

        return securityDao.getUserInfoByUserLogin(login, true);
    }

    public UserNameDto getUserNameById(Long userId) {
        return securityDao.getUserNameById(userId);
    }
}
