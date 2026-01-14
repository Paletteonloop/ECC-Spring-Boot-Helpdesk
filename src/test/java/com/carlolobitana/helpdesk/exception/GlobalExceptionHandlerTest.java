package com.carlolobitana.helpdesk.exception;

import com.carlolobitana.helpdesk.controller.EmployeeController;
import com.carlolobitana.helpdesk.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class GlobalExceptionHandlerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private EmployeeService employeeService;

    @Test
    void handleAllExceptions_Returns500() throws Exception {
        // Simulate a generic runtime error
        when(employeeService.getAllEmployees()).thenThrow(new RuntimeException("Database down"));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Internal Server Error")));
    }
}