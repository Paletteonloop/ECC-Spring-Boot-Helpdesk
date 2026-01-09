package com.carlolobitana.helpdesk.controller;


import com.carlolobitana.helpdesk.dto.EmployeeRequestDTO;
import com.carlolobitana.helpdesk.dto.EmployeeResponseDTO;
import com.carlolobitana.helpdesk.dto.EmployeeStatsDTO;
import com.carlolobitana.helpdesk.exception.ResourceNotFoundException;
import com.carlolobitana.helpdesk.service.EmployeeService;


import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void createEmployee_ReturnsOk() throws Exception {
        EmployeeRequestDTO request = new EmployeeRequestDTO();
        request.setFirstName("Mike");

        EmployeeResponseDTO response = new EmployeeResponseDTO();
        response.setId(1L);
        response.setName("Mike Ross");

        when(employeeService.createEmployee(any())).thenReturn(response);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mike Ross"));
    }

    @Test
    void getAllEmployees_ReturnsList() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getEmployeeById_NotFound_Returns404() throws Exception {
        when(employeeService.getEmployeeById(99L))
                .thenThrow(new ResourceNotFoundException("Employee with ID 99 not found"));

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Error: Employee with ID 99 not found")));
    }

    @Test
    void getStats_ReturnsStatsDto() throws Exception {
        EmployeeStatsDTO stats = new EmployeeStatsDTO("Harvey Specter", 5, 2, 10);
        when(employeeService.getEmployeePerformance(1L)).thenReturn(stats);

        mockMvc.perform(get("/api/employees/stats/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeName").value("Harvey Specter"))
                .andExpect(jsonPath("$.closedCount").value(10));
    }

    @Test
    void deleteEmployee_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());
    }
}