package com.saguro.rapid.configserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saguro.rapid.configserver.dto.UserDTO;
import com.saguro.rapid.configserver.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.saguro.rapid.configserver.components.JwtAuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void getAllUsersReturnsList() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUsername("user");
        Mockito.when(userService.getAllUsers()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("user"));
    }

    @Test
    void createUserReturnsCreated() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setUsername("user");
        Mockito.when(userService.createUser(Mockito.any(UserDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/users")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"));
    }
}
