package com.carlolobitana.helpdesk.dto;

import com.carlolobitana.helpdesk.enums.EmploymentStatus;
import lombok.Data;
import java.util.Set;

@Data
public class EmployeeDTO {
    private Long id;
    private String name;
    private String address;
    private EmploymentStatus employmentStatus;
    private Set<String> roles;
}