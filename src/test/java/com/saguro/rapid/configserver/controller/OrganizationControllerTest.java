package com.saguro.rapid.configserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saguro.rapid.configserver.dto.OrganizationDTO;
import com.saguro.rapid.configserver.service.OrganizationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = OrganizationController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrganizationService organizationService;

    @Test
    void getAllOrganizationsReturnsList() throws Exception {
        OrganizationDTO dto = new OrganizationDTO();
        dto.setId(1L);
        dto.setName("Org");
        Mockito.when(organizationService.getAllOrganizations()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/organizations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Org"));
    }

    @Test
    void createOrganizationReturnsCreated() throws Exception {
        OrganizationDTO dto = new OrganizationDTO();
        dto.setName("Org");
        Mockito.when(organizationService.createOrganization(Mockito.any(OrganizationDTO.class)))
                .thenReturn(dto);

        mockMvc.perform(post("/api/organizations")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Org"));
    }
}
