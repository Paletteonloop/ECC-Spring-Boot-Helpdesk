package com.carlolobitana.helpdesk.service.impl;


import com.carlolobitana.helpdesk.dto.ContactInfoDTO;
import com.carlolobitana.helpdesk.dto.EmployeeRequestDTO;
import com.carlolobitana.helpdesk.dto.EmployeeResponseDTO;
import com.carlolobitana.helpdesk.dto.EmployeeStatsDTO;
import com.carlolobitana.helpdesk.enums.EmploymentStatus;
import com.carlolobitana.helpdesk.exception.ResourceNotFoundException;
import com.carlolobitana.helpdesk.model.ContactInfo;
import com.carlolobitana.helpdesk.model.Employee;
import com.carlolobitana.helpdesk.model.FullName;
import com.carlolobitana.helpdesk.model.Role;
import com.carlolobitana.helpdesk.repository.EmployeeRepository;
import com.carlolobitana.helpdesk.repository.RoleRepository;
import com.carlolobitana.helpdesk.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private RoleRepository roleRepository;

    @InjectMocks private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setName(new FullName("John", "Quincy", "Doe"));
        employee.setDeleted(false);
        employee.setContactInfos(new ArrayList<>()); // Initialize for branch testing

        requestDTO = new EmployeeRequestDTO();
        requestDTO.setFirstName("Jane");
        requestDTO.setLastName("Smith");
        requestDTO.setAge(30);
        requestDTO.setEmploymentStatus(EmploymentStatus.REGULAR);
    }

    // --- CREATE / UPDATE BRANCHES ---

    @Test
    void createEmployee_WithRolesAndContacts_FullCoverage() {
        // Setup DTO with roles and contacts to trigger loops
        requestDTO.setRoleIds(Set.of(101L));
        ContactInfoDTO contact = new ContactInfoDTO();
        contact.setType("EMAIL");
        contact.setValue("test@test.com");
        requestDTO.setContactInfos(List.of(contact));

        Role role = new Role();
        role.setId(101L);
        role.setName("ADMIN");

        when(roleRepository.findAllById(any())).thenReturn(List.of(role));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> i.getArguments()[0]);

        EmployeeResponseDTO result = employeeService.createEmployee(requestDTO);

        assertNotNull(result);
        assertTrue(result.getRoles().contains("ADMIN"));
        assertEquals(1, result.getContactInfos().size());
        verify(employeeRepository).save(any());
    }

    @Test
    void updateEmployee_ClearsExistingContacts() {
        // Scenario: Employee already has one contact, update provides none
        ContactInfo existingInfo = new ContactInfo();
        employee.getContactInfos().add(existingInfo);

        when(employeeRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        employeeService.updateEmployee(1L, requestDTO);

        // Branch check: mapDtoToEntity should have called clear()
        assertTrue(employee.getContactInfos().isEmpty());
    }

    // --- MAPPING BRANCHES (mapToResponse) ---

    @Test
    void mapToResponse_HandlesNullMiddleName_Branch() {
        // Branch check: middleName is null
        employee.setName(new FullName("John", null, "Doe"));
        when(employeeRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(employee));

        EmployeeResponseDTO result = employeeService.getEmployeeById(1L);

        // Logic check: "John  Doe".replace("  ", " ") -> "John Doe"
        assertEquals("John Doe", result.getName());
    }

    @Test
    void mapToResponse_HandlesEmptyRolesAndContacts() {
        employee.setRoles(null);
        employee.setContactInfos(null);
        when(employeeRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(employee));

        EmployeeResponseDTO result = employeeService.getEmployeeById(1L);

        assertNull(result.getRoles());
        assertNull(result.getContactInfos());
    }

    // --- EXCEPTION BRANCHES ---

    @Test
    void updateEmployee_NotFound_ThrowsException() {
        when(employeeRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee(1L, requestDTO));
    }

    @Test
    void getEmployeePerformance_NotFound_ThrowsException() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeePerformance(1L));
    }

    @Test
    void getAllEmployees_ReturnsList() {
        when(employeeRepository.findByDeletedFalse()).thenReturn(List.of(employee));
        List<EmployeeResponseDTO> results = employeeService.getAllEmployees();
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }
}