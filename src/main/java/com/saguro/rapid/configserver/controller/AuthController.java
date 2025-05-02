package com.saguro.rapid.configserver.controller;

import com.saguro.rapid.configserver.dto.LoginRequest;
import com.saguro.rapid.configserver.dto.LoginResponse;
import com.saguro.rapid.configserver.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.authenticate(
            loginRequest.getUsername(), 
            loginRequest.getPassword()
        );
        return ResponseEntity.ok(response);
    }
}
