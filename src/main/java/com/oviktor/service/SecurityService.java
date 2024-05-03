package com.oviktor.service;

import com.oviktor.dto.UserInfo;
import com.oviktor.dto.UserNameDto;

import java.util.Optional;

public interface SecurityService {

    Optional<UserInfo> checkCredentialsAndReturnUserInfo(String login, String password);

    UserNameDto getUserNameById(Long userId);
}


