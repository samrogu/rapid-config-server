package com.saguro.rapid.configserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saguro.rapid.configserver.dto.UserPermissionDTO;
import com.saguro.rapid.configserver.entity.UserPermission;
import com.saguro.rapid.configserver.mapper.UserPermissionMapper;
import com.saguro.rapid.configserver.service.UserPermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserPermissionControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserPermissionService userPermissionService;

    @Mock
    private UserPermissionMapper userPermissionMapper;

    @InjectMocks
    private UserPermissionController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Simula un principal autenticado
        var auth = new UsernamePasswordAuthenticationToken(
            "user",
            "N/A",
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
    }

    @Test
    void getPermissionsByUsernameReturnsList() throws Exception {
        UserPermission entity = new UserPermission();
        entity.setId(1L);

        UserPermissionDTO dto = new UserPermissionDTO();
        dto.setId(1L);
        dto.setUsername("user");

        when(userPermissionService.getPermissionsByUsername("user"))
            .thenReturn(Collections.singletonList(entity));
        when(userPermissionMapper.toDTO(entity))
            .thenReturn(dto);

        mockMvc.perform(get("/api/permissions/user/user"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andExpect(jsonPath("$[0].id").value(1))
               .andExpect(jsonPath("$[0].username").value("user"));
    }
}
