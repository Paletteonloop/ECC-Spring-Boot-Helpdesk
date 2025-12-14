package com.carlolobitana.helpdesk.repository;

import com.carlolobitana.helpdesk.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}