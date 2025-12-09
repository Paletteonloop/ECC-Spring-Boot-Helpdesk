package com.carlolobitana.helpdesk.service.impl;

import com.carlolobitana.helpdesk.model.Role;
import com.carlolobitana.helpdesk.repository.RoleRepository;
import com.carlolobitana.helpdesk.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public List<Role> viewAllRoles() {
        return roleRepository.findAll();
    }


    public Role viewRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }


    public Role createRole(String name) {
        Role newRole = new Role(name);
        return roleRepository.save(newRole);
    }


    public void updateRole(Long id, String name) {
        roleRepository.findById(id).ifPresent(role -> {
            // Update roe properties here
            role.setName(name);
            roleRepository.save(role);
        });
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}
    