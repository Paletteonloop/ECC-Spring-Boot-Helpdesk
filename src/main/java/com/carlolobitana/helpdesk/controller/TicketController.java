package com.carlolobitana.helpdesk.controller;

import com.carlolobitana.helpdesk.dto.TicketRequestDTO;
import com.carlolobitana.helpdesk.dto.TicketResponseDTO;
import com.carlolobitana.helpdesk.dto.TicketSearchDTO;
import com.carlolobitana.helpdesk.enums.TicketStatus;
import com.carlolobitana.helpdesk.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping("/file")
    public ResponseEntity<TicketResponseDTO> fileTicket(@RequestBody TicketRequestDTO dto) {
        return ResponseEntity.ok(ticketService.fileTicket(dto));
    }

    @GetMapping
    public ResponseEntity<Page<TicketResponseDTO>> getAllTickets(
            TicketSearchDTO search,
            @PageableDefault(size = 15, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ticketService.getTickets(search, pageable));
    }

    @GetMapping("/{ticketNumber}")
    public ResponseEntity<TicketResponseDTO> getOneTicket(@PathVariable String ticketNumber) {
        TicketResponseDTO ticketDto = ticketService.getTicketById(ticketNumber);
        return ResponseEntity.ok(ticketDto);
    }

    @PutMapping("/{ticketNumber}/assign")
    public ResponseEntity<TicketResponseDTO> assignTicket(
            @PathVariable String ticketNumber,
            @RequestParam Long employeeId,
            @RequestParam Long updaterId) {
        return ResponseEntity.ok(ticketService.assignTicket(ticketNumber, employeeId, updaterId));
    }

    @PutMapping("/{ticketNumber}/status")
    public ResponseEntity<TicketResponseDTO> updateStatus(
            @PathVariable String ticketNumber,
            @RequestParam TicketStatus status,
            @RequestParam(required = false) String remarks,
            @RequestParam Long updaterId) {
        return ResponseEntity.ok(ticketService.updateTicket(ticketNumber, status, remarks, updaterId));
    }
}