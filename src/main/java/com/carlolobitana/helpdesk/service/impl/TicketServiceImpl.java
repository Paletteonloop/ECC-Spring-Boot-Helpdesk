package com.carlolobitana.helpdesk.service.impl;

import com.carlolobitana.helpdesk.model.Ticket;
import com.carlolobitana.helpdesk.repository.TicketRepository;
import com.carlolobitana.helpdesk.service.TicketService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TicketServiceImpl implements TicketService {

    private TicketRepository ticketRepository;

    @Override
    public List<Ticket> viewAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public Ticket viewTicketById(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    @Override
    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
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

    @Override
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }
}
