package com.saguro.rapid.configserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saguro.rapid.configserver.dto.OrganizationDTO;
import com.saguro.rapid.configserver.service.OrganizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrganizationControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private OrganizationService organizationService;

    @Mock
    private com.saguro.rapid.configserver.service.UserPermissionService userPermissionService;

    @InjectMocks
    private OrganizationController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void getAllOrganizationsReturnsList() throws Exception {
        OrganizationDTO dto = new OrganizationDTO();
        dto.setId(1L);
        dto.setName("Org");

        when(organizationService.getOrganizationsForUser(any(), any()))
                .thenReturn(Collections.singletonList(dto));

        // Mock permission check for getOrganizationsForUser inside controller is not
        // needed
        // because the controller delegates to service directly for filtering.
        // But wait, the controller calls
        // organizationService.getOrganizationsForUser(username, userPermissionService)

        // Create a mock Authentication
        org.springframework.security.core.Authentication auth = org.mockito.Mockito
                .mock(org.springframework.security.core.Authentication.class);
        when(auth.getName()).thenReturn("user");

        mockMvc.perform(get("/api/organizations").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Org"));
    }

    @Test
    void createOrganizationReturnsCreated() throws Exception {
        OrganizationDTO dto = new OrganizationDTO();
        dto.setName("Org");

        // Usamos any(OrganizationDTO.class) para que el mock capture cualquier DTO
        // entrante
        when(organizationService.createOrganization(any(OrganizationDTO.class)))
                .thenReturn(dto);

        mockMvc.perform(post("/api/organizations")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                // ahora sí encontrará el campo name en el body
                .andExpect(jsonPath("$.name").value("Org"));
    }
}