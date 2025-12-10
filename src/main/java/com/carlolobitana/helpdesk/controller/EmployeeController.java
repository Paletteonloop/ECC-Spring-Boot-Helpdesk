package com.carlolobitana.helpdesk.controller;

import com.carlolobitana.helpdesk.model.Employee;
import com.carlolobitana.helpdesk.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/helpdesk/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public List<Employee> viewAllEmployees() {
        return employeeService.viewAllEmployees();
    }

    @GetMapping("/{id}")
    public Employee viewEmployeeById(@PathVariable Long id) {
        return employeeService.viewEmployeeById(id);
    }

    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.createEmployee(employee);
    }

    @PatchMapping("/{id}")
    public Employee updateEmployee(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        return employeeService.updateEmployee(id, updates);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }


}