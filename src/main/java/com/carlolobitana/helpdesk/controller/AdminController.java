package com.carlolobitana.helpdesk.controller;

// For populating Repositories: POST http://localhost:8080/api/admin/init

import com.carlolobitana.helpdesk.enums.TicketStatus;
import com.carlolobitana.helpdesk.model.Employee;
import com.carlolobitana.helpdesk.model.Role;
import com.carlolobitana.helpdesk.enums.EmploymentStatus;
import com.carlolobitana.helpdesk.model.Ticket;
import com.carlolobitana.helpdesk.repository.EmployeeRepository;
import com.carlolobitana.helpdesk.repository.RoleRepository;
import com.carlolobitana.helpdesk.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final int NUMBER_OF_TICKETS = 50; // Define total tickets to create

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private TicketRepository ticketRepository;

    @PostMapping("/init")
    public String initData() {
        // Clear previous data for repeatable tests
        ticketRepository.deleteAll();
        employeeRepository.deleteAll();
        roleRepository.deleteAll();

        // 1. Create Roles
        Role adminRole = createRole("ADMIN");
        Role supportRole = createRole("IT_SUPPORT");
        Role empRole = createRole("EMPLOYEE");

        // 2. Create Employees (IDs 1, 2, 3)
        // Since no Spring Security, no password is needed.
        Employee admin = createEmployee("Alice (Admin)", adminRole); // ID 1
        Employee support = createEmployee("Bob (Support)", supportRole); // ID 2
        Employee regular = createEmployee("Charlie (Regular)", empRole); // ID 3

        // 3. Create Sample Tickets (Mass generation loop)
        LocalDateTime initialDate = LocalDateTime.now().minusDays(30);

        for (int i = 1; i <= NUMBER_OF_TICKETS; i++) {
            TicketStatus status = getStatusForIndex(i);
            Employee assignee = getAssigneeForIndex(i, admin, support);

            // Create the ticket and set its date to be sequentially older
            Ticket t = createTicket(
                    "TICKET " + i + ": Issue Summary",
                    "Detailed body for ticket number " + i + ".",
                    regular,
                    status,
                    assignee,
                    assignee != null ? assignee.getName() : null
            );

            // Set the creation date backward to ensure reliable sorting (newest first)
            t.setCreatedDate(initialDate.plusMinutes(i));

            ticketRepository.save(t);
        }

        return String.format(
                "Data Initialized. Created 3 Employees and %d Sample Tickets (IDs are dynamic Strings).",
                NUMBER_OF_TICKETS
        );
    }

    // --- Helper Methods ---

    private Role createRole(String name) {
        Role role = new Role();
        role.setName(name);
        return roleRepository.save(role);
    }

    private Employee createEmployee(String name, Role role) {
        Employee emp = new Employee();
        emp.setName(name);
        emp.setAge(30);
        emp.setEmploymentStatus(EmploymentStatus.REGULAR);
        emp.setRoles(Set.of(role));
        return employeeRepository.save(emp);
    }

    private Ticket createTicket(String title, String body, Employee creator, TicketStatus status, Employee assignee, String updatedBy) {
        Ticket ticket = new Ticket();
        ticket.setTitle(title);
        ticket.setBody(body);
        ticket.setCreatedBy(creator);
        ticket.setStatus(status);
        // ticketNumber is auto-generated via @PrePersist in the Ticket entity.
        ticket.setAssignee(assignee);
        ticket.setUpdatedBy(updatedBy);

        return ticket;
    }

    private TicketStatus getStatusForIndex(int i) {
        // Creates a mix of statuses for filtering tests
        if (i % 5 == 0) return TicketStatus.CLOSED;
        if (i % 4 == 0) return TicketStatus.IN_PROGRESS;
        return TicketStatus.FILED;
    }

    private Employee getAssigneeForIndex(int i, Employee admin, Employee support) {
        // Creates a mix of assigned and unassigned tickets
        if (i % 3 == 0) return admin; // Assigned to Admin
        if (i % 2 == 0) return support; // Assigned to Support
        return null; // Unassigned
    }
}