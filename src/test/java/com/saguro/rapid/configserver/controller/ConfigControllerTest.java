package com.saguro.rapid.configserver.controller;

import com.saguro.rapid.configserver.components.DynamicConfigComponent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ConfigController.class)
@AutoConfigureMockMvc(addFilters = false)
class ConfigControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DynamicConfigComponent dynamicConfigComponent;

    @Test
    void getConfigReturnsEnvironment() throws Exception {
        Environment env = new Environment("ms", "default");
        Mockito.when(dynamicConfigComponent.findOne("org/app/ms", "default", "main"))
                .thenReturn(env);

        mockMvc.perform(get("/config/v1/org/app/ms/default/main"))
                .andExpect(status().isOk());
    }
}
