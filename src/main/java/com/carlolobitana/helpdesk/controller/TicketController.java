package com.carlolobitana.helpdesk.controller;

import com.carlolobitana.helpdesk.model.Ticket;
import com.carlolobitana.helpdesk.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/helpdesk/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @GetMapping
    public List<Ticket> viewAllTickets() {
        return ticketService.viewAllTickets();
    }

    @GetMapping("/{id}")
    public Ticket viewTicketById(@PathVariable Long id) {
        return ticketService.viewTicketById(id);
    }

    @PostMapping
    public Ticket createTicket(@RequestBody Ticket ticket) {
        return ticketService.createTicket(ticket);
    }

    @PatchMapping("/{id}")
    public Ticket updateTicket(@PathVariable Long id, @RequestBody Map<String , Object> updates) {
        return ticketService.updateTicket(id, updates);
    }

}
