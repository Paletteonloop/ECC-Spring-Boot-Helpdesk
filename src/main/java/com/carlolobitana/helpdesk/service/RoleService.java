package com.carlolobitana.helpdesk.service;

import com.carlolobitana.helpdesk.model.Role;
import java.util.List;


public interface RoleService {

    List<Role> viewAllRoles();
    Role viewRoleById(Long id);
    Role createRole(String name);
    void updateRole(Long id, String name);
    void deleteRole(Long id);

}
