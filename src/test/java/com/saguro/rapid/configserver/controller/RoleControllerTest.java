package com.saguro.rapid.configserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saguro.rapid.configserver.dto.RoleDTO;
import com.saguro.rapid.configserver.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RoleControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
    }

    @Test
    void getAllRolesReturnsList() throws Exception {
        RoleDTO dto = new RoleDTO();
        dto.setId(1L);
        dto.setName("ROLE_ADMIN");

        when(roleService.getAllRoles())
            .thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/roles"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(1))
               .andExpect(jsonPath("$[0].name").value("ROLE_ADMIN"));
    }
}
