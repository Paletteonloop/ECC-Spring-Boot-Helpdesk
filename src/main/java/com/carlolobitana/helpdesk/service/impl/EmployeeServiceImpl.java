package com.carlolobitana.helpdesk.service.impl;

import com.carlolobitana.helpdesk.model.Employee;
import com.carlolobitana.helpdesk.repository.EmployeeRepository;
import com.carlolobitana.helpdesk.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> viewAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee viewEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Map<String, Object> updates) {
        Employee employee = employeeRepository.findById(id).orElse(null);

        updates.forEach((key, value) -> {;
            switch (key) {
                case "name":
                    employee.setName((String) value);
                    break;
                case "age":
                    employee.setAge((Integer) value);
                    break;
                case "address":
                    employee.setAddress((String) value);
                    break;
                case "phoneNumber":
                    employee.setPhoneNumber((String) value);
                    break;
            }
        });
        return employeeRepository.save(employee);

    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public void assignRoleToEmployee(Long employeeId, Long roleId) {

    }
}
