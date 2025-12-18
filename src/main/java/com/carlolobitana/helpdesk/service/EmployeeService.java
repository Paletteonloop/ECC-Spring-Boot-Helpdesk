package com.carlolobitana.helpdesk.service;

import com.carlolobitana.helpdesk.dto.EmployeeRequestDTO;
import com.carlolobitana.helpdesk.dto.EmployeeResponseDTO;
import com.carlolobitana.helpdesk.dto.EmployeeStatsDTO;

import java.util.List;


public interface EmployeeService {

    EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto);
    List<EmployeeResponseDTO> getAllEmployees();
    EmployeeResponseDTO getEmployeeById(Long id);
    EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto);
    void deleteEmployee(Long id);
    EmployeeStatsDTO getEmployeePerformance(Long employeeId);
}
