package com.carlolobitana.helpdesk.repository;

import com.carlolobitana.helpdesk.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}