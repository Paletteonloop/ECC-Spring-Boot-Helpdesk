package com.carlolobitana.helpdesk.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FullName {
    private String firstName;
    private String middleName;
    private String lastName;
}