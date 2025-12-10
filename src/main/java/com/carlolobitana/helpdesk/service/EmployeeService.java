package com.carlolobitana.helpdesk.service;

import com.carlolobitana.helpdesk.model.Employee;
import com.carlolobitana.helpdesk.model.Role;

import java.util.List;
import java.util.Map;

public interface EmployeeService {

    List<Employee> viewAllEmployees();
    Employee viewEmployeeById(Long id);
    Employee createEmployee(Employee employee);
    Employee updateEmployee(Long id, Map<String, Object> updates);
    void deleteEmployee(Long id);
//void assignRoleToEmployee(Long employeeId, Role role);

}
