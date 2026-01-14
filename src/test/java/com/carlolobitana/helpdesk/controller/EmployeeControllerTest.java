package com.carlolobitana.helpdesk.controller;


import com.carlolobitana.helpdesk.dto.EmployeeRequestDTO;
import com.carlolobitana.helpdesk.dto.EmployeeResponseDTO;
import com.carlolobitana.helpdesk.dto.EmployeeStatsDTO;
import com.carlolobitana.helpdesk.exception.ResourceNotFoundException;
import com.carlolobitana.helpdesk.service.EmployeeService;


import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeResponseDTO responseDTO;
    private EmployeeRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new EmployeeResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Carlo Lobitana");
        responseDTO.setRoles(Set.of("ADMIN"));

        requestDTO = new EmployeeRequestDTO();
        requestDTO.setFirstName("Carlo");
        requestDTO.setLastName("Lobitana");
    }

    @Test
    void createEmployee_ReturnsOk() throws Exception {
        when(employeeService.createEmployee(any(EmployeeRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Carlo Lobitana"));
    }

    @Test
    void getAllEmployees_ReturnsList() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Carlo Lobitana"));
    }

    @Test
    void getEmployeeById_Success() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getEmployeeById_NotFound_Returns404() throws Exception {
        // This test also covers GlobalExceptionHandler.handleResourceNotFound
        when(employeeService.getEmployeeById(99L))
                .thenThrow(new ResourceNotFoundException("Employee not found"));

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Error: Employee not found"));
    }

    @Test
    void updateEmployee_ReturnsUpdatedObject() throws Exception {
        when(employeeService.updateEmployee(eq(1L), any(EmployeeRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Carlo Lobitana"));
    }

    @Test
    void deleteEmployee_ReturnsNoContent() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getStats_ReturnsStatsDTO() throws Exception {
        EmployeeStatsDTO stats = new EmployeeStatsDTO("Carlo", 5L, 2L, 10L);
        when(employeeService.getEmployeePerformance(1L)).thenReturn(stats);

        mockMvc.perform(get("/api/employees/stats/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeName").value("Carlo"))
                .andExpect(jsonPath("$.closedCount").value(10));
    }

    @Test
    void handleAllExceptions_Returns500() throws Exception {
        // This test covers the generic Exception handler in GlobalExceptionHandler
        when(employeeService.getAllEmployees()).thenThrow(new RuntimeException("Database down"));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal Server Error: Database down"));
    }
}