package com.carlolobitana.helpdesk.model;

import com.carlolobitana.helpdesk.enums.EmploymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded //maps firstName, middleName, and lastName to Employee table
    private FullName name;

    private Integer age;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContactInfo> contactInfos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EmploymentStatus employmentStatus;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "employee_roles")
    private Set<Role> roles = new HashSet<>();

    private boolean deleted = false;
}