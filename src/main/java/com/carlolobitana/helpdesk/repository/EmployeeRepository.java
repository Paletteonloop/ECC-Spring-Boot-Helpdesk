package com.carlolobitana.helpdesk.repository;

import com.carlolobitana.helpdesk.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByDeletedFalse();
    Optional<Employee> findByIdAndDeletedFalse(Long id);
}