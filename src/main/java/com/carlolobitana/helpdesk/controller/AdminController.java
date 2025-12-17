package com.carlolobitana.helpdesk.controller;

import com.carlolobitana.helpdesk.enums.EmploymentStatus;
import com.carlolobitana.helpdesk.enums.TicketStatus;
import com.carlolobitana.helpdesk.model.*;
import com.carlolobitana.helpdesk.repository.EmployeeRepository;
import com.carlolobitana.helpdesk.repository.RoleRepository;
import com.carlolobitana.helpdesk.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private TicketRepository ticketRepository;

    @PostMapping("/init")
    public String initData() {
        ticketRepository.deleteAll();
        employeeRepository.deleteAll();
        roleRepository.deleteAll();

        // 1. Create Roles
        Role admin = createRole("ADMIN");
        Role support = createRole("IT_SUPPORT");
        Role emp = createRole("EMPLOYEE");
        Role manager = createRole("MANAGER");
        Role auditor = createRole("AUDITOR");

        // 2. Create Employees with Mixed Roles
        // We'll store them in a list to pick random creators/assignees later
        List<Employee> allEmployees = new ArrayList<>();

        allEmployees.add(createEmployee("John", "Doe", Set.of(admin, manager))); // Multi-role
        allEmployees.add(createEmployee("Jane", "Smith", Set.of(support, emp))); // Multi-role
        allEmployees.add(createEmployee("Mike", "Ross", Set.of(support)));
        allEmployees.add(createEmployee("Rachel", "Zane", Set.of(emp, auditor))); // Multi-role
        allEmployees.add(createEmployee("Harvey", "Specter", Set.of(admin)));
        allEmployees.add(createEmployee("Donna", "Paulsen", Set.of(manager, admin, support))); // 3 roles
        allEmployees.add(createEmployee("Louis", "Litt", Set.of(auditor)));
        allEmployees.add(createEmployee("Kevin", "Hart", Set.of(emp)));
        allEmployees.add(createEmployee("Bill", "Burr", Set.of(emp)));
        allEmployees.add(createEmployee("Tom", "Segura", Set.of(support)));

        // 3. Generate 100 Tickets
        LocalDateTime initialDate = LocalDateTime.now().minusDays(60);
        Random random = new Random();

        for (int i = 1; i <= 100; i++) {
            Ticket t = new Ticket();
            t.setTitle("System Issue #" + i);
            t.setBody("Automated log for ticket number " + i + ". Testing pagination and specifications.");

            // Randomly assign a creator and an optional assignee
            Employee creator = allEmployees.get(random.nextInt(allEmployees.size()));
            Employee assignee = (i % 3 == 0) ? null : allEmployees.get(random.nextInt(allEmployees.size()));

            t.setCreatedBy(creator);
            t.setAssignee(assignee);
            t.setStatus(getStatusForIndex(i));

            if (assignee != null) {
                t.setUpdatedBy(assignee.getName().getFirstName() + " " + assignee.getName().getLastName());
            }

            // Staggered dates for sorting tests
            t.setCreatedDate(initialDate.plusHours(i * 2));
            ticketRepository.save(t);
        }

        return "Data Initialized: 5 Roles, 10 Employees (with mixed roles), and 100 Tickets.";
    }

    private Role createRole(String name) {
        Role role = new Role();
        role.setName(name);
        return roleRepository.save(role);
    }

    private Employee createEmployee(String fName, String lName, Set<Role> roles) {
        Employee emp = new Employee();
        emp.setName(new FullName(fName, "", lName));

        ContactInfo info = new ContactInfo();
        info.setAddress(fName + " St., Tech City");
        info.setContactNumber("555-" + (1000 + new Random().nextInt(9000)));
        emp.setContactInfo(info);

        emp.setAge(22 + new Random().nextInt(30));
        emp.setEmploymentStatus(EmploymentStatus.REGULAR);
        emp.setRoles(roles);
        emp.setDeleted(false);

        return employeeRepository.save(emp);
    }

    private TicketStatus getStatusForIndex(int i) {
        if (i % 10 == 0) return TicketStatus.CLOSED;
        if (i % 4 == 0) return TicketStatus.IN_PROGRESS;
        return TicketStatus.FILED;
    }
}