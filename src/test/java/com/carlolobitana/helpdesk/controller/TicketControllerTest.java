package com.carlolobitana.helpdesk.controller;


import com.carlolobitana.helpdesk.dto.TicketRequestDTO;
import com.carlolobitana.helpdesk.dto.TicketResponseDTO;
import com.carlolobitana.helpdesk.dto.TicketSearchDTO;
import com.carlolobitana.helpdesk.enums.TicketStatus;
import com.carlolobitana.helpdesk.service.TicketService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean
    private TicketService ticketService;

    @Test
    void fileTicket_ReturnsOk() throws Exception {
        TicketRequestDTO request = new TicketRequestDTO();
        request.setTitle("Login Issue");

        TicketResponseDTO response = new TicketResponseDTO();
        response.setTicketNumber("T-ABC");
        response.setTitle("Login Issue");

        when(ticketService.fileTicket(any())).thenReturn(response);

        mockMvc.perform(post("/api/tickets/file")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketNumber").value("T-ABC"));
    }

    @Test
    void getAllTickets_SupportsPagination() throws Exception {
        TicketResponseDTO ticket = new TicketResponseDTO();
        ticket.setTicketNumber("T-1");
        PageImpl<TicketResponseDTO> page = new PageImpl<>(Collections.singletonList(ticket));

        when(ticketService.getTickets(any(TicketSearchDTO.class), any())).thenReturn(page);

        mockMvc.perform(get("/api/tickets")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].ticketNumber").value("T-1"));
    }

    @Test
    void assignTicket_WithRequestParams_ReturnsOk() throws Exception {
        TicketResponseDTO response = new TicketResponseDTO();
        response.setAssigneeName("Harvey Specter");
        response.setStatus(TicketStatus.IN_PROGRESS);

        when(ticketService.assignTicket(eq("T-123"), eq(1L), eq(2L))).thenReturn(response);

        mockMvc.perform(put("/api/tickets/T-123/assign")
                        .param("employeeId", "1")
                        .param("updaterId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assigneeName").value("Harvey Specter"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void updateStatus_ReturnsUpdatedTicket() throws Exception {
        TicketResponseDTO response = new TicketResponseDTO();
        response.setStatus(TicketStatus.CLOSED);

        when(ticketService.updateTicket(eq("T-123"), eq(TicketStatus.CLOSED), anyString(), eq(1L)))
                .thenReturn(response);

        mockMvc.perform(put("/api/tickets/T-123/status")
                        .param("status", "CLOSED")
                        .param("remarks", "Resolved")
                        .param("updaterId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }
}