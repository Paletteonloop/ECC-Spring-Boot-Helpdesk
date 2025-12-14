package com.carlolobitana.helpdesk.dto;

import lombok.Data;

@Data
public class TicketRequestDTO {
    private String title;
    private String body;
    private Long creatorId;
}