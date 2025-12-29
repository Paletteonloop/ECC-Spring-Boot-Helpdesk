package com.carlolobitana.helpdesk.model;

import com.carlolobitana.helpdesk.enums.EmploymentStatus;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Data
@SQLRestriction("deleted = false")
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