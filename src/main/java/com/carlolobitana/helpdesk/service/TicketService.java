package com.carlolobitana.helpdesk.service;

public interface TicketService {

    void viewAllTickets();
    void viewTicketById(Long id);
    void createTicket();
    void updateTicket(Long id);
    void deleteTicket(Long id);
}
