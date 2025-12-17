package com.carlolobitana.helpdesk.dto;

import com.carlolobitana.helpdesk.enums.EmploymentStatus;
import lombok.Data;
import java.util.Set;

@Data
public class EmployeeRequestDTO {
    private String firstName;
    private String middleName;
    private String lastName;
    private Integer age;
    private String address;
    private String contactNumber;
    private EmploymentStatus employmentStatus;
    private Set<Long> roleIds;
}