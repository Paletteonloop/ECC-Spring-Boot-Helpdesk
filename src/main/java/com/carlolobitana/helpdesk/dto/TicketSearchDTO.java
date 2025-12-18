package com.carlolobitana.helpdesk.dto;

import com.carlolobitana.helpdesk.enums.TicketStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TicketSearchDTO {
    private TicketStatus status;
    private Long assigneeId;
    private Long creatorId;
    private String titleKeyword;
    private LocalDate startDate;
    private LocalDate endDate;
}
