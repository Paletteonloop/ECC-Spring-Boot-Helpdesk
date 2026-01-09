package com.carlolobitana.helpdesk.service.impl;


import com.carlolobitana.helpdesk.dto.EmployeeRequestDTO;
import com.carlolobitana.helpdesk.dto.EmployeeResponseDTO;
import com.carlolobitana.helpdesk.dto.EmployeeStatsDTO;
import com.carlolobitana.helpdesk.exception.ResourceNotFoundException;
import com.carlolobitana.helpdesk.model.Employee;
import com.carlolobitana.helpdesk.model.FullName;
import com.carlolobitana.helpdesk.repository.EmployeeRepository;
import com.carlolobitana.helpdesk.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private TicketRepository ticketRepository;

    @InjectMocks private EmployeeServiceImpl employeeService;

    @Test
    void getEmployeeById_ValidId_ReturnsDto() {
        //Arrange
        Employee emp = new Employee();
        emp.setId(1L);
        emp.setName(new FullName("John", "", "Doe"));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));

        //Act
        EmployeeResponseDTO result = employeeService.getEmployeeById(1L);

        //Assert
        assertEquals("John Doe", result.getName());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void createEmployee_SavesWithCorrectMapping() {
        //Arrange
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setFirstName("Harvey");
        dto.setLastName("Specter");
        dto.setAge(45);

        when(employeeRepository.save(any(Employee.class))).thenAnswer(i -> {
            Employee e = i.getArgument(0);
            e.setId(10L); // Simulate DB auto-gen ID
            return e;
        });

        //Act
        EmployeeResponseDTO result = employeeService.createEmployee(dto);

        //Assert
        assertNotNull(result.getId());
        assertEquals("Harvey Specter", result.getName());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void deleteEmployee_PerformsSoftDelete() {
        //Arrange
        Employee emp = new Employee();
        emp.setId(1L);
        emp.setDeleted(false);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));

        //Act
        employeeService.deleteEmployee(1L);

        //Assert: Verify the 'deleted' flag was flipped before saving
        ArgumentCaptor<Employee> captor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(captor.capture());
        assertTrue(captor.getValue().isDeleted());
    }

    @Test
    void getAllEmployees_ReturnsOnlyActiveMappedDTOs() {
        //Arrange
        Employee e1 = new Employee();
        e1.setName(new FullName("Louis", "M", "Litt"));

        when(employeeRepository.findByDeletedFalse()).thenReturn(Arrays.asList(e1));

        //Act
        List<EmployeeResponseDTO> results = employeeService.getAllEmployees();

        //Assert
        assertEquals(1, results.size());
        assertEquals("Louis M Litt", results.get(0).getName());
    }

    @Test
    void getEmployeeById_InvalidId_ThrowsException() {
        //Arrange
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(99L));
    }

    @Test
    void getEmployeePerformance_ReturnsCorrectStats() {
        //Arrange
        Employee emp = new Employee();
        emp.setName(new FullName("Mike", "", "Ross"));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));
        when(ticketRepository.countByAssigneeAndStatus(1L, "FILED")).thenReturn(5L);
        when(ticketRepository.countByAssigneeAndStatus(1L, "IN_PROGRESS")).thenReturn(2L);
        when(ticketRepository.countByAssigneeAndStatus(1L, "CLOSED")).thenReturn(10L);

        //Act
        EmployeeStatsDTO stats = employeeService.getEmployeePerformance(1L);

        //Assert
        assertEquals("Mike Ross", stats.getEmployeeName());
        assertEquals(10, stats.getClosedCount());
    }
}