package com.saguro.rapid.configserver.controller;

import com.saguro.rapid.configserver.components.DynamicConfigComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConfigControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DynamicConfigComponent dynamicConfigComponent;

    @InjectMocks
    private ConfigController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            // Registramos convertidor para serializar Environment a JSON si lo necesitases
            .setMessageConverters(new MappingJackson2HttpMessageConverter())
            .build();
    }

    @Test
    void getConfigReturnsEnvironment() throws Exception {
        Environment env = new Environment("ms", "default");
        // Stub del componente
        when(dynamicConfigComponent.findOne("org/app/ms", "default", "main"))
            .thenReturn(env);

        mockMvc.perform(get("/config/v1/org/app/ms/default/main"))
               .andExpect(status().isOk());
    }
}
