package com.carlolobitana.helpdesk.service.impl;

import com.carlolobitana.helpdesk.service.EmployeeService;
import com.carlolobitana.helpdesk.dto.EmployeeRequestDTO;
import com.carlolobitana.helpdesk.dto.EmployeeResponseDTO;
import com.carlolobitana.helpdesk.model.Employee;
import com.carlolobitana.helpdesk.model.Role;
import com.carlolobitana.helpdesk.repository.EmployeeRepository;
import com.carlolobitana.helpdesk.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto) {
        Employee employee = new Employee();
        mapDtoToEntity(dto, employee);
        return mapToResponse(employeeRepository.save(employee));
    }

    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
        return mapToResponse(employee);
    }

    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
        mapDtoToEntity(dto, employee);
        return mapToResponse(employeeRepository.save(employee));
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found");
        }
        employeeRepository.deleteById(id);
    }

    private void mapDtoToEntity(EmployeeRequestDTO dto, Employee employee) {
        employee.setName(dto.getName());
        employee.setAge(dto.getAge());
        employee.setAddress(dto.getAddress());
        employee.setContactNumber(dto.getContactNumber());
        employee.setEmploymentStatus(dto.getEmploymentStatus());
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            List<Role> roles = roleRepository.findAllById(dto.getRoleIds());
            employee.setRoles(new HashSet<>(roles));
        }
    }

    private EmployeeResponseDTO mapToResponse(Employee employee) {
        EmployeeResponseDTO response = new EmployeeResponseDTO();
        response.setId(employee.getId());
        response.setName(employee.getName());
        response.setAge(employee.getAge());
        response.setAddress(employee.getAddress());
        response.setContactNumber(employee.getContactNumber());
        response.setEmploymentStatus(employee.getEmploymentStatus());
        if (employee.getRoles() != null) {
            response.setRoles(employee.getRoles().stream()
                    .map(Role::getName).collect(Collectors.toSet()));
        }
        return response;
    }
}