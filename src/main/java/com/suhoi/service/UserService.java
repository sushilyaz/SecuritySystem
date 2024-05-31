package com.suhoi.service;

import com.suhoi.dto.AuthDto;
import com.suhoi.dto.UserCreateDto;

public interface UserService {
    void auth(AuthDto dto);
    void save(UserCreateDto user);
}
