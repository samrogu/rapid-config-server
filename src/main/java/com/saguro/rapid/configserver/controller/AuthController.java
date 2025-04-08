package com.saguro.rapid.configserver.controller;

import com.saguro.rapid.configserver.dto.AuthRequestDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import com.saguro.rapid.configserver.service.JwtService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AuthRequestDTO authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            // Generar el token JWT
            String token = jwtService.generateToken(authentication);

            return Map.of("token", token);
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }
}
