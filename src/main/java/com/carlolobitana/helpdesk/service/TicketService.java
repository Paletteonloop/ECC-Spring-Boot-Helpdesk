package com.carlolobitana.helpdesk.service;

import com.carlolobitana.helpdesk.dto.TicketRequestDTO;
import com.carlolobitana.helpdesk.dto.TicketResponseDTO;
import com.carlolobitana.helpdesk.dto.TicketSearchDTO;
import com.carlolobitana.helpdesk.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketService {

    TicketResponseDTO fileTicket(TicketRequestDTO dto);
    Page<TicketResponseDTO> getTickets(TicketSearchDTO search, Pageable pageable);
    TicketResponseDTO getTicketById(String ticketNumber);
    TicketResponseDTO assignTicket(String ticketNumber, Long employeeId, Long updaterId);
    TicketResponseDTO updateTicket(String ticketNumber, TicketStatus status, String remarks, Long updaterId);
}
