package com.carlolobitana.helpdesk.model;

import com.carlolobitana.helpdesk.enums.TicketStatus;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Ticket {
    @Id
    @Column(unique = true, updatable = false)
    private String ticketNumber;

    private String title;
    private String body;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private Employee assignee;

    @ManyToOne
    @JoinColumn(name = "created_by_id", updatable = false)
    private Employee createdBy;

    //System generated timestamps
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

    private String updatedBy;

    private String remarks;

    @PrePersist //will generate ticketNumber before creation
    protected void generateTicketNumber() {
        if (this.ticketNumber == null) {
            this.ticketNumber = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}