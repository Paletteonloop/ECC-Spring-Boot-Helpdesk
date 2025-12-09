package com.carlolobitana.helpdesk.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class Ticket {
    @Id
    private Long id;
    private String ticketNumber;
    private String title;
    private String body;
    private String assignee;
    private String status;
    private Date dateCreated;
    private String createdBy;
    private Date dateUpdated;
    private String updatedBy;
    private String remarks;

    public Ticket(Long id, String ticketNumber, String title, String body, String assignee, String status,
                  Date dateCreated, String createdBy, Date dateUpdated, String updatedBy, String remarks) {
        this.id = id;
        this.ticketNumber = ticketNumber;
        this.title = title;
        this.body = body;
        this.assignee = assignee;
        this.status = status;
        this.dateCreated = dateCreated;
        this.createdBy = createdBy;
        this.dateUpdated = dateUpdated;
        this.updatedBy = updatedBy;
        this.remarks = remarks;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", ticketNumber='" + ticketNumber + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", assignee='" + assignee + '\'' +
                ", status='" + status + '\'' +
                ", dateCreated=" + dateCreated +
                ", createdBy='" + createdBy + '\'' +
                ", dateUpdated=" + dateUpdated +
                ", updatedBy='" + updatedBy + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }
}
