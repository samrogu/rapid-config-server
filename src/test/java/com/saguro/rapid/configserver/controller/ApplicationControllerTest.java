package com.saguro.rapid.configserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saguro.rapid.configserver.dto.ApplicationDTO;
import com.saguro.rapid.configserver.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ApplicationControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private ApplicationController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // Construimos MockMvc en modo standalone, sin arrancar Spring
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .build();
    }

    @Test
    void getAllApplicationsReturnsList() throws Exception {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(1L);
        dto.setName("App");

        when(applicationService.getAllApplications())
            .thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/applications"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(APPLICATION_JSON))
               .andExpect(jsonPath("$[0].id").value(1))
               .andExpect(jsonPath("$[0].name").value("App"));
    }

    @Test
    void createApplicationReturnsCreated() throws Exception {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setName("App");

        when(applicationService.createApplication(eq(2L), any(ApplicationDTO.class)))
            .thenReturn(dto);

        mockMvc.perform(post("/api/applications/organization/2")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
               .andExpect(status().isOk())
               .andExpect(content().contentType(APPLICATION_JSON))
               .andExpect(jsonPath("$.name").value("App"));
    }
}