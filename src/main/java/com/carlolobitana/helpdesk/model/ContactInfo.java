package com.carlolobitana.helpdesk.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ContactInfo {
    private String address;
    private String contactNumber;
}