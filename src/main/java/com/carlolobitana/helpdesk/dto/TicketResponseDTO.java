package com.carlolobitana.helpdesk.dto;

import com.carlolobitana.helpdesk.enums.TicketStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketResponseDTO {
    private String ticketNumber;
    private String title;
    private String body;
    private TicketStatus status;
    private String assigneeName;
    private String createdByName;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String updatedBy;
    private String remarks;
}