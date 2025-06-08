package com.saguro.rapid.configserver.controller;

import com.saguro.rapid.configserver.dto.UserPermissionDTO;
import com.saguro.rapid.configserver.entity.UserPermission;
import com.saguro.rapid.configserver.mapper.UserPermissionMapper;
import com.saguro.rapid.configserver.service.UserPermissionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserPermissionController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserPermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserPermissionService userPermissionService;

    @MockBean
    private UserPermissionMapper userPermissionMapper;

    @Test
    void getPermissionsByUsernameReturnsList() throws Exception {
        UserPermission permission = new UserPermission();
        permission.setId(1L);
        UserPermissionDTO dto = new UserPermissionDTO();
        dto.setId(1L);
        dto.setUsername("user");
        Mockito.when(userPermissionService.getPermissionsByUsername("user"))
                .thenReturn(Collections.singletonList(permission));
        Mockito.when(userPermissionMapper.toDTO(permission)).thenReturn(dto);

        mockMvc.perform(get("/api/permissions/user/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("user"));
    }
}
