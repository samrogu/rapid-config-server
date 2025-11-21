package com.saguro.rapid.configserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saguro.rapid.configserver.dto.LoginRequest;
import com.saguro.rapid.configserver.dto.LoginResponse;
import com.saguro.rapid.configserver.dto.UserInfo;
import com.saguro.rapid.configserver.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                // Solo registramos el converter para JSON
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                // OMITIMOS el filtro de seguridad
                .build();
    }

    @Test
    void loginReturnsTokenAndUser() throws Exception {
        // given
        LoginRequest request = new LoginRequest();
        request.setUsername("user");
        request.setPassword("pass");

        UserInfo userInfo = new UserInfo(1L, "user", Set.of("ROLE_USER"), false, java.util.Collections.emptyList());
        LoginResponse response = new LoginResponse("token", userInfo);
        when(authService.authenticate("user", "pass")).thenReturn(response);

        // when / then
        mockMvc.perform(post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                // Aserciones sobre el JSON
                .andExpect(jsonPath("$.token").value("token"))
                .andExpect(jsonPath("$.userInfo.username").value("user"));
    }
}
