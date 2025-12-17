package com.carlolobitana.helpdesk.model;

import com.carlolobitana.helpdesk.enums.EmploymentStatus;

import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Data
@SQLRestriction("deleted = false")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded // 📢 This maps firstName, middleName, and lastName to the Employee table
    private FullName name;

    private Integer age;

    @Embedded // 📢 Fields from ContactInfo will be added to the Employee table
    private ContactInfo contactInfo;

    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();

    private boolean deleted = false;
}