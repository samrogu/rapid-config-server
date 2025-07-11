package com.saguro.rapid.configserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saguro.rapid.configserver.dto.LoginRequest;
import com.saguro.rapid.configserver.dto.LoginResponse;
import com.saguro.rapid.configserver.dto.UserInfo;
import com.saguro.rapid.configserver.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.saguro.rapid.configserver.components.JwtAuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void loginReturnsTokenAndUser() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("pass");
        LoginResponse response = new LoginResponse("token", new UserInfo(1L, "user", Set.of("ROLE_USER")));
        Mockito.when(authService.authenticate("user", "pass")).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"))
                .andExpect(jsonPath("$.userInfo.username").value("user"));
    }
}
