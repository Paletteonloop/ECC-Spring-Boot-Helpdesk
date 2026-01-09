package com.carlolobitana.helpdesk.service.impl;

import com.carlolobitana.helpdesk.dto.ContactInfoDTO;
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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@AllArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeRepository employeeRepository;

    private TicketRepository ticketRepository;

    private RoleRepository roleRepository;

    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto) {
        Employee employee = new Employee();
        mapDtoToEntity(dto, employee);
        return mapToResponse(employeeRepository.save(employee));
    }

    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findByDeletedFalse().stream().map(this::mapToResponse).collect(Collectors.toList());
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

        employee.setName(new FullName(dto.getFirstName(), dto.getMiddleName(), dto.getLastName()));

        if (employee.getContactInfos() != null) {
            employee.getContactInfos().clear();
        }

        if (dto.getContactInfos() != null) {
            dto.getContactInfos().forEach(cDto -> {
                ContactInfo info = new ContactInfo();
                info.setContactType(cDto.getType());
                info.setDetails(cDto.getValue());
                info.setEmployee(employee);
                employee.getContactInfos().add(info);
            });
        }

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

        response.setEmploymentStatus(employee.getEmploymentStatus());

        if (employee.getRoles() != null) {
            Set<String> roleNames = employee.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet());
            response.setRoles(roleNames);
        }

        if (employee.getContactInfos() != null) {
            List<ContactInfoDTO> contactList = employee.getContactInfos().stream().map(c -> {
                ContactInfoDTO cDto = new ContactInfoDTO();
                cDto.setType(c.getContactType());
                cDto.setValue(c.getDetails());
                return cDto;
            }).collect(Collectors.toList());
            response.setContactInfos(contactList);
        }

        return response;
    }
}