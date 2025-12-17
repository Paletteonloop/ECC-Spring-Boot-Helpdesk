package com.carlolobitana.helpdesk.service.impl;

import com.carlolobitana.helpdesk.dto.TicketRequestDTO;
import com.carlolobitana.helpdesk.dto.TicketResponseDTO;
import com.carlolobitana.helpdesk.exception.ResourceNotFoundException;
import com.carlolobitana.helpdesk.model.Employee;
import com.carlolobitana.helpdesk.model.FullName;
import com.carlolobitana.helpdesk.model.Ticket;
import com.carlolobitana.helpdesk.enums.TicketStatus;
import com.carlolobitana.helpdesk.repository.EmployeeRepository;
import com.carlolobitana.helpdesk.repository.TicketRepository;
import com.carlolobitana.helpdesk.repository.TicketSpecification;
import com.carlolobitana.helpdesk.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class TicketServiceImpl implements TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public TicketResponseDTO fileTicket(TicketRequestDTO dto) {
        Employee creator = employeeRepository.findById(dto.getCreatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        Ticket ticket = new Ticket();
        ticket.setTitle(dto.getTitle());
        ticket.setBody(dto.getBody());
        ticket.setCreatedBy(creator);
        ticket.setStatus(TicketStatus.FILED);
        ticket.setTicketNumber(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return mapToResponse(ticketRepository.save(ticket));
    }

    public Page<TicketResponseDTO> getTickets(TicketStatus status, Long assigneeId, Pageable pageable) {
        Page<Ticket> tickets = ticketRepository.findAll(TicketSpecification.filterTickets(status, assigneeId), pageable);
        return tickets.map(this::mapToResponse);
    }

    public TicketResponseDTO getTicketById(String ticketNumber) {
        Ticket ticket = ticketRepository.findById(ticketNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketNumber));
        return mapToResponse(ticket);
    }

    public TicketResponseDTO assignTicket(String ticketNumber, Long employeeId, Long updaterId) {
        Ticket ticket = ticketRepository.findById(ticketNumber).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        Employee assignee = employeeRepository.findById(employeeId).orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
        Employee updater = employeeRepository.findById(updaterId).orElseThrow(() -> new ResourceNotFoundException("Updater not found"));
        ticket.setAssignee(assignee);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket.setUpdatedBy(formatFullName(updater.getName()));
        return mapToResponse(ticketRepository.save(ticket));
    }

    public TicketResponseDTO updateTicket(String ticketNumber, TicketStatus status, String remarks, Long updaterId) {
        Ticket ticket = ticketRepository.findById(ticketNumber).orElseThrow();
        Employee updater = employeeRepository.findById(updaterId).orElseThrow();
        if(status != null) ticket.setStatus(status);
        if(remarks != null) ticket.setRemarks(remarks);
        ticket.setUpdatedBy(formatFullName(updater.getName()));
        return mapToResponse(ticketRepository.save(ticket));
    }

    private TicketResponseDTO mapToResponse(Ticket ticket) {
        TicketResponseDTO dto = new TicketResponseDTO();
        dto.setTicketNumber(ticket.getTicketNumber());
        dto.setTitle(ticket.getTitle());
        dto.setBody(ticket.getBody());
        dto.setStatus(ticket.getStatus());
        dto.setCreatedDate(ticket.getCreatedDate());
        dto.setUpdatedDate(ticket.getUpdatedDate());
        dto.setUpdatedBy(ticket.getUpdatedBy());
        dto.setRemarks(ticket.getRemarks());
        if (ticket.getAssignee() != null) {
            dto.setAssigneeName(formatFullName(ticket.getAssignee().getName()));
        }
        if (ticket.getCreatedBy() != null) {
            dto.setCreatedByName(formatFullName(ticket.getCreatedBy().getName()));
        }
        return dto;
    }

    private String formatFullName(FullName name) {
        if (name == null) return "Unknown";
        return String.format("%s %s", name.getFirstName(), name.getLastName()).trim();
    }
}