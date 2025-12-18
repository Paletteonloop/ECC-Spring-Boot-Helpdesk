package com.carlolobitana.helpdesk.service.impl;

import com.carlolobitana.helpdesk.dto.EmployeeStatsDTO;
import com.carlolobitana.helpdesk.enums.TicketStatus;
import com.carlolobitana.helpdesk.exception.ResourceNotFoundException;
import com.carlolobitana.helpdesk.model.ContactInfo;
import com.carlolobitana.helpdesk.model.FullName;
import com.carlolobitana.helpdesk.repository.TicketRepository;
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
    private TicketRepository ticketRepository;

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
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + id + " not found"));
        return mapToResponse(employee);
    }

    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + id + " not found"));
        mapDtoToEntity(dto, employee);
        return mapToResponse(employeeRepository.save(employee));
    }

    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + id + " not found"));
        employee.setDeleted(true);
        employeeRepository.save(employee);
    }

    public EmployeeStatsDTO getEmployeePerformance(Long employeeId) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        long filed = ticketRepository.countByAssigneeAndStatus(employeeId, TicketStatus.FILED.name());
        long progress = ticketRepository.countByAssigneeAndStatus(employeeId, TicketStatus.IN_PROGRESS.name());
        long closed = ticketRepository.countByAssigneeAndStatus(employeeId, TicketStatus.CLOSED.name());

        String fullName = emp.getName().getFirstName() + " " + emp.getName().getLastName();

        return new EmployeeStatsDTO(fullName, filed, progress, closed);
    }

    private void mapDtoToEntity(EmployeeRequestDTO dto, Employee employee) {
        FullName fullName = new FullName(
                dto.getFirstName(),
                dto.getMiddleName(),
                dto.getLastName()
        );
        employee.setName(fullName);

        employee.setAge(dto.getAge());

        ContactInfo info = new ContactInfo();
        info.setAddress(dto.getAddress());
        info.setContactNumber(dto.getContactNumber());
        employee.setContactInfo(info);

        employee.setEmploymentStatus(dto.getEmploymentStatus());
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            List<Role> roles = roleRepository.findAllById(dto.getRoleIds());
            employee.setRoles(new HashSet<>(roles));
        }
    }

    private EmployeeResponseDTO mapToResponse(Employee employee) {
        EmployeeResponseDTO response = new EmployeeResponseDTO();
        response.setId(employee.getId());

        if (employee.getName() != null) {
            String full = String.format("%s %s %s",
                    employee.getName().getFirstName(),
                    employee.getName().getMiddleName() != null ? employee.getName().getMiddleName() : "",
                    employee.getName().getLastName()).replace("  ", " ");
            response.setName(full.trim());
        }

        response.setAge(employee.getAge());

        if (employee.getContactInfo() != null) {
            response.setAddress(employee.getContactInfo().getAddress());
            response.setContactNumber(employee.getContactInfo().getContactNumber());
        }

        response.setEmploymentStatus(employee.getEmploymentStatus());
        if (employee.getRoles() != null) {
            response.setRoles(employee.getRoles().stream()
                    .map(Role::getName).collect(Collectors.toSet()));
        }
        return response;
    }
}