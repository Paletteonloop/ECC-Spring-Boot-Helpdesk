package com.carlolobitana.helpdesk.controller;


import com.carlolobitana.helpdesk.dto.TicketRequestDTO;
import com.carlolobitana.helpdesk.dto.TicketResponseDTO;
import com.carlolobitana.helpdesk.dto.TicketSearchDTO;
import com.carlolobitana.helpdesk.enums.TicketStatus;
import com.carlolobitana.helpdesk.exception.ResourceNotFoundException;
import com.carlolobitana.helpdesk.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketService ticketService;

    @Autowired
    private ObjectMapper objectMapper;

    private TicketResponseDTO responseDTO;
    private final String TICKET_NO = "TKT-12345";

    @BeforeEach
    void setUp() {
        responseDTO = new TicketResponseDTO();
        responseDTO.setTicketNumber(TICKET_NO);
        responseDTO.setTitle("Test Ticket");
        responseDTO.setStatus(TicketStatus.FILED);
    }

    @Test
    void fileTicket_Success() throws Exception {
        TicketRequestDTO requestDTO = new TicketRequestDTO();
        requestDTO.setTitle("Test Ticket");

        when(ticketService.fileTicket(any(TicketRequestDTO.class))).thenReturn(responseDTO);

        // FIX: Match the /file sub-path in your controller
        mockMvc.perform(post("/api/tickets/file")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketNumber").value(TICKET_NO));
    }

    @Test
    void getAllTickets_Success() throws Exception {
        when(ticketService.getTickets(any(TicketSearchDTO.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(responseDTO)));

        mockMvc.perform(get("/api/tickets")
                        .param("page", "0")
                        .param("size", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].ticketNumber").value(TICKET_NO));
    }

    @Test
    void assignTicket_Success() throws Exception {
        when(ticketService.assignTicket(eq(TICKET_NO), anyLong(), anyLong())).thenReturn(responseDTO);

        // FIX: Match the @PutMapping used in your controller
        mockMvc.perform(put("/api/tickets/" + TICKET_NO + "/assign")
                        .param("employeeId", "1")
                        .param("updaterId", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void updateStatus_WithRemarks() throws Exception {
        when(ticketService.updateTicket(eq(TICKET_NO), any(TicketStatus.class), anyString(), anyLong()))
                .thenReturn(responseDTO);

        // FIX: Match the @PutMapping used in your controller
        mockMvc.perform(put("/api/tickets/" + TICKET_NO + "/status")
                        .param("status", "IN_PROGRESS")
                        .param("remarks", "Taking this up")
                        .param("updaterId", "2"))
                .andExpect(status().isOk());
    }

    @Test
    void getOneTicket_Success() throws Exception {
        // 1. Arrange: Define what the service should return
        when(ticketService.getTicketById(TICKET_NO)).thenReturn(responseDTO);

        // 2. Act & Assert: Call the GET endpoint with the ticket number
        mockMvc.perform(get("/api/tickets/{ticketNumber}", TICKET_NO))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ticketNumber").value(TICKET_NO))
                .andExpect(jsonPath("$.title").value("Test Ticket"))
                .andExpect(jsonPath("$.status").value("FILED"));

        // Verify service was called exactly once
        verify(ticketService, times(1)).getTicketById(TICKET_NO);
    }

    @Test
    void getOneTicket_NotFound_Returns404() throws Exception {
        // 1. Arrange: Force the service to throw the custom exception
        String invalidTicket = "NON-EXISTENT";
        when(ticketService.getTicketById(invalidTicket))
                .thenThrow(new ResourceNotFoundException("Ticket not found with number: " + invalidTicket));

        // 2. Act & Assert: Expect 404 Not Found from your GlobalExceptionHandler
        mockMvc.perform(get("/api/tickets/{ticketNumber}", invalidTicket))
                .andExpect(status().isNotFound())
                // Depending on your GlobalExceptionHandler body format:
                .andExpect(content().string(containsString("Ticket not found")));
    }
}