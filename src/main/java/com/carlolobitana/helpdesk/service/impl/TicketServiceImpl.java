package com.carlolobitana.helpdesk.service.impl;

import com.carlolobitana.helpdesk.model.Ticket;
import com.carlolobitana.helpdesk.repository.TicketRepository;
import com.carlolobitana.helpdesk.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public List<Ticket> viewAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket viewTicketById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public Ticket updateTicket(Long id, Map<String, Object> updates) {
        Ticket ticket = ticketRepository.findById(id).orElse(null);

        updates.forEach((key, value) -> {
            switch (key) {
                case "ticketNumber":
                    ticket.setTicketNumber((Long) value);
                    break;
                case "title":
                    ticket.setTitle((String) value);
                    break;
                case "body":
                    ticket.setBody((String) value);
                    break;
                case "assignee":
                    ticket.setAssignee((String) value);
                    break;
                case "status":
                    ticket.setStatus((String) value);
                    break;
                case "createdBy":
                    ticket.setCreatedBy((String) value);
                    break;
                case "updatedBy":
                    ticket.setUpdatedBy((String) value);
                    break;
                case "remarks":
                    ticket.setRemarks((String) value);
                    break;
            }
        });

        return ticketRepository.save(ticket);

    }

    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    public Ticket assignTicket(Long id, String assignee) {
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        ticket.setAssignee(assignee);
        return ticketRepository.save(ticket);
    }

    public Ticket changeTicketStatus(Long id, String status) {
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        ticket.setStatus(status);
        return ticketRepository.save(ticket);
    }

    public Ticket addTicketRemarks(Long id, String remarks) {
        Ticket ticket = ticketRepository.findById(id).orElse(null);
        ticket.setRemarks(remarks);
        return ticketRepository.save(ticket);
    }
}
