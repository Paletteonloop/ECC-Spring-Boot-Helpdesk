package com.carlolobitana.helpdesk.service.impl;


import com.carlolobitana.helpdesk.dto.EmployeeResponseDTO;
import com.carlolobitana.helpdesk.dto.EmployeeStatsDTO;
import com.carlolobitana.helpdesk.exception.ResourceNotFoundException;
import com.carlolobitana.helpdesk.model.Employee;
import com.carlolobitana.helpdesk.model.FullName;
import com.carlolobitana.helpdesk.repository.EmployeeRepository;
import com.carlolobitana.helpdesk.repository.RoleRepository;
import com.carlolobitana.helpdesk.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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