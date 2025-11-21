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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConfigControllerTest {

        private MockMvc mockMvc;

        @Mock
        private DynamicConfigComponent dynamicConfigComponent;

        @Mock
        private com.saguro.rapid.configserver.service.ApplicationService applicationService;

        @Mock
        private com.saguro.rapid.configserver.service.UserPermissionService userPermissionService;

        @InjectMocks
        private ConfigController controller;

        @BeforeEach
        void setup() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders
                                .standaloneSetup(controller)
                                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                                .build();
        }

        @Test
        void getConfigReturnsEnvironment() throws Exception {
                Environment env = new Environment("ms", "default");
                String org = "org";
                String app = "app";
                String label = "main";
                String username = "user";
                Long appId = 1L;

                // Mock Application lookup
                com.saguro.rapid.configserver.entity.Application appEntity = new com.saguro.rapid.configserver.entity.Application();
                appEntity.setId(appId);
                when(applicationService.findByOrganizationAndUidAndMicroservice(org, app, label))
                                .thenReturn(java.util.Optional.of(appEntity));

                // Mock Permissions
                when(userPermissionService.isAdmin(username)).thenReturn(false);
                when(userPermissionService.canReadApplication(
                                any(org.springframework.security.core.Authentication.class),
                                eq(appId)))
                                .thenReturn(true);

                // Stub del componente
                when(dynamicConfigComponent.findOne("org/app/ms", "default", "main"))
                                .thenReturn(env);

                // Create a mock Authentication
                org.springframework.security.core.Authentication auth = mock(
                                org.springframework.security.core.Authentication.class);
                when(auth.getName()).thenReturn(username);

                mockMvc.perform(get("/config/v1/org/app/ms/default/main").principal(auth))
                                .andExpect(status().isOk());
        }
}
