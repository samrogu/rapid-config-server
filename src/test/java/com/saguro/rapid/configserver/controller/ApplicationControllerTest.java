package com.saguro.rapid.configserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saguro.rapid.configserver.dto.ApplicationDTO;
import com.saguro.rapid.configserver.service.ApplicationService;
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

@WebMvcTest(controllers = ApplicationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ApplicationService applicationService;

    @Test
    void getAllApplicationsReturnsList() throws Exception {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(1L);
        dto.setName("App");
        Mockito.when(applicationService.getAllApplications()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("App"));
    }

    @Test
    void createApplicationReturnsCreated() throws Exception {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setName("App");
        Mockito.when(applicationService.createApplication(Mockito.eq(2L), Mockito.any(ApplicationDTO.class)))
                .thenReturn(dto);

        mockMvc.perform(post("/api/applications/organization/2")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("App"));
    }
}
