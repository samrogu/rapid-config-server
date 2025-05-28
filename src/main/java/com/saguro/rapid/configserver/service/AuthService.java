package com.saguro.rapid.configserver.service;

import com.saguro.rapid.configserver.dto.LoginResponse;

public interface AuthService {
    LoginResponse authenticate(String username, String password);
}
