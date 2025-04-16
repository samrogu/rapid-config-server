package com.saguro.rapid.configserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/config")
public class LoginController {

    private final AuthenticationManager authenticationManager;

    public LoginController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticate(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Si la autenticación es exitosa, devolver un mensaje de éxito
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("username", username);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            // Si la autenticación falla, devolver un mensaje de error
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Invalid username or password");
            return ResponseEntity.status(401).body(response);
        }
    }
}
