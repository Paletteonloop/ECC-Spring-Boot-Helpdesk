package com.carlolobitana.helpdesk.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


import com.carlolobitana.helpdesk.dto.TicketRequestDTO;
import com.carlolobitana.helpdesk.enums.TicketStatus;

import com.carlolobitana.helpdesk.dto.TicketResponseDTO;
import com.carlolobitana.helpdesk.model.Employee;
import com.carlolobitana.helpdesk.model.FullName;
import com.carlolobitana.helpdesk.model.Ticket;
import com.carlolobitana.helpdesk.repository.EmployeeRepository;
import com.carlolobitana.helpdesk.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private EmployeeRepository employeeRepository;

    @InjectMocks private TicketServiceImpl ticketService;

    @Test
    void assignTicket_UpdatesStatusAndAssignee() {
        Ticket ticket = new Ticket();
        ticket.setTicketNumber("T-123");
        ticket.setStatus(TicketStatus.FILED);

        Employee assignee = new Employee();
        assignee.setName(new FullName("Harvey", "", "Specter"));

        Employee updater = new Employee();
        updater.setName(new FullName("Donna", "", "Paulsen"));

        when(ticketRepository.findById("T-123")).thenReturn(Optional.of(ticket));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(assignee));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(updater));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArguments()[0]);

        TicketResponseDTO result = ticketService.assignTicket("T-123", 1L, 2L);

        assertEquals(TicketStatus.IN_PROGRESS, result.getStatus());
        assertEquals("Harvey Specter", result.getAssigneeName());
        assertEquals("Donna Paulsen", ticket.getUpdatedBy());
    }

    @Test
    void fileTicket_SetsInitialStatusAndGeneratesNumber() {
        Employee creator = new Employee();
        creator.setId(5L);
        creator.setName(new FullName("Donna", "", "Paulsen"));

        TicketRequestDTO dto = new TicketRequestDTO();
        dto.setCreatorId(5L);
        dto.setTitle("Printer Broken");
        dto.setBody("Paper jam on floor 2");

        when(employeeRepository.findById(5L)).thenReturn(Optional.of(creator));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        TicketResponseDTO result = ticketService.fileTicket(dto);

        assertEquals(TicketStatus.FILED, result.getStatus());
        assertNotNull(result.getTicketNumber());
        assertEquals(8, result.getTicketNumber().length()); // Verifying UUID substring logic
        assertEquals("Donna Paulsen", result.getCreatedByName());
    }

    @Test
    void updateTicket_UpdatesOnlyProvidedFields() {
        Ticket existingTicket = new Ticket();
        existingTicket.setTicketNumber("ABC-123");
        existingTicket.setStatus(TicketStatus.IN_PROGRESS);
        existingTicket.setRemarks("Old Remark");

        Employee updater = new Employee();
        updater.setName(new FullName("Harvey", "", "Specter"));

        when(ticketRepository.findById("ABC-123")).thenReturn(Optional.of(existingTicket));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(updater));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

        TicketResponseDTO result = ticketService.updateTicket("ABC-123", null, "New Remark", 1L);

        assertEquals("New Remark", result.getRemarks());
        assertEquals(TicketStatus.IN_PROGRESS, result.getStatus());
        assertEquals("Harvey Specter", existingTicket.getUpdatedBy());
    }
}