package com.carlolobitana.helpdesk.dto;

import com.carlolobitana.helpdesk.enums.EmploymentStatus;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class EmployeeResponseDTO {
    private Long id;
    private String name;
    private Integer age;
    private List<ContactInfoDTO> contactInfos;
    private EmploymentStatus employmentStatus;
    private Set<String> roles;
}