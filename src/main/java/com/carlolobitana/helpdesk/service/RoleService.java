package com.carlolobitana.helpdesk.service;

import com.carlolobitana.helpdesk.dto.RoleDTO;
import java.util.List;

public interface RoleService {

    RoleDTO createRole(RoleDTO dto);
    List<RoleDTO> getAllRoles();
    RoleDTO getRoleById(Long id);
    RoleDTO updateRole(Long id, RoleDTO dto);
    void deleteRole(Long id);
}
