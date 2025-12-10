package com.carlolobitana.helpdesk.service;

import com.carlolobitana.helpdesk.model.Ticket;

import java.util.List;
import java.util.Map;

public interface TicketService {

    List<Ticket> viewAllTickets();
    Ticket viewTicketById(Long id);
    Ticket createTicket(Ticket ticket);
    Ticket updateTicket(Long id, Map<String , Object> updates);
    void deleteTicket(Long id);
    Ticket assignTicket(Long id, String assignee);
    Ticket changeTicketStatus(Long id, String status);
    Ticket addTicketRemarks(Long id, String remarks);
}
