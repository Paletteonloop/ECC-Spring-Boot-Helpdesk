package com.carlolobitana.helpdesk.controller;

import com.carlolobitana.helpdesk.model.Role;
import com.carlolobitana.helpdesk.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/helpdesk/roles")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping
    public List<Role> viewAllRoles() {
        return roleService.viewAllRoles();
    }

    @GetMapping("/{id}")
    public Role viewRoleById(@PathVariable Long id) {
        return roleService.viewRoleById(id);
    }

    @PostMapping
    public Role createRole(@RequestBody String name) {
        return roleService.createRole(name);
    }

    @PutMapping("/{id}")
    public void updateRole(@PathVariable Long id, @RequestBody String name) {
        roleService.updateRole(id, name);
    }

    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
    }



}
