package com.carlolobitana.helpdesk.controller;

import com.carlolobitana.helpdesk.dto.RoleDTO;
import com.carlolobitana.helpdesk.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    private RoleDTO roleDTO;

    @BeforeEach
    void setUp() {
        roleDTO = new RoleDTO();
        roleDTO.setId(1L);
        roleDTO.setName("IT_SUPPORT");
    }

    @Test
    void createRole_ReturnsOk() throws Exception {
        // Stubbing for a method that returns a value
        when(roleService.createRole(any(RoleDTO.class))).thenReturn(roleDTO);

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDTO))) // content() should now resolve
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("IT_SUPPORT"));
    }

    @Test
    void deleteRole_ReturnsNoContent() throws Exception {
        // Correct syntax for void method stubbing
        doNothing().when(roleService).deleteRole(1L);

        mockMvc.perform(delete("/api/roles/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllRoles_ReturnsList() throws Exception {
        when(roleService.getAllRoles()).thenReturn(List.of(roleDTO));

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("IT_SUPPORT"));
    }

    @Test
    void getRoleById_ReturnsOk() throws Exception {
        when(roleService.getRoleById(1L)).thenReturn(roleDTO);

        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateRole_ReturnsUpdatedRole() throws Exception {
        // 1. Prepare the update data
        RoleDTO updateRequest = new RoleDTO();
        updateRequest.setName("SUPER_ADMIN");

        // 2. Prepare the expected response
        RoleDTO updatedResponse = new RoleDTO();
        updatedResponse.setId(1L);
        updatedResponse.setName("SUPER_ADMIN");

        // 3. Stub with more flexible matchers to ensure it hits
        when(roleService.updateRole(eq(1L), any(RoleDTO.class))).thenReturn(updatedResponse);

        // 4. Perform request
        mockMvc.perform(put("/api/roles/1") // Ensure this matches eq(1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))) // Ensure this isn't null
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("SUPER_ADMIN")); // This should now pass
    }
}