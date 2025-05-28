/* package com.saguro.rapid.configserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/config")
public class LoginController {

    private final UserDetailsService userDetailsService;

    public LoginController(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/login")
    public ResponseEntity<Map<String, Object>> authenticate(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("username", username);
            response.put("authorities", userDetails.getAuthorities());
            response.put("accountNonExpired", userDetails.isAccountNonExpired());
            response.put("accountNonLocked", userDetails.isAccountNonLocked());
            response.put("credentialsNonExpired", userDetails.isCredentialsNonExpired());
            response.put("enabled", userDetails.isEnabled());
            
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            // Si la autenticación falla, devolver un mensaje de error
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Invalid username or password");
            return ResponseEntity.status(401).body(response);
        }
    }
}
 */