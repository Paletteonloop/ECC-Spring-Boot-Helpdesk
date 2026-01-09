package com.carlolobitana.helpdesk.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


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
        //Arrange
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

        //Act
        TicketResponseDTO result = ticketService.assignTicket("T-123", 1L, 2L);

        //Assert
        assertEquals(TicketStatus.IN_PROGRESS, result.getStatus());
        assertEquals("Harvey Specter", result.getAssigneeName());
        assertEquals("Donna Paulsen", ticket.getUpdatedBy());
    }
}