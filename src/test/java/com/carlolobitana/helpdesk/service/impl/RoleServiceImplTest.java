package com.carlolobitana.helpdesk.service.impl;

import com.carlolobitana.helpdesk.dto.RoleDTO;
import com.carlolobitana.helpdesk.model.Role;
import com.carlolobitana.helpdesk.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role sampleRole;
    private RoleDTO sampleDto;

    @BeforeEach
    void setUp() {
        sampleRole = new Role();
        sampleRole.setId(1L);
        sampleRole.setName("ADMIN");

        sampleDto = new RoleDTO();
        sampleDto.setId(1L);
        sampleDto.setName("ADMIN");
    }

    @Test
    void createRole_Success() {
        when(roleRepository.save(any(Role.class))).thenReturn(sampleRole);

        RoleDTO result = roleService.createRole(sampleDto);

        assertNotNull(result);
        assertEquals("ADMIN", result.getName());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void getAllRoles_ReturnsList() {
        when(roleRepository.findAll()).thenReturn(List.of(sampleRole));

        List<RoleDTO> result = roleService.getAllRoles();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("ADMIN", result.get(0).getName());
    }

    @Test
    void getAllRoles_EmptyList() {
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());

        List<RoleDTO> result = roleService.getAllRoles();

        assertTrue(result.isEmpty());
    }

    @Test
    void getRoleById_Success() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(sampleRole));

        RoleDTO result = roleService.getRoleById(1L);

        assertEquals(1L, result.getId());
        assertEquals("ADMIN", result.getName());
    }

    @Test
    void getRoleById_NotFound_ThrowsException() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                roleService.getRoleById(99L));

        assertEquals("Role not found", exception.getMessage());
    }

    @Test
    void updateRole_Success() {
        RoleDTO updateDto = new RoleDTO();
        updateDto.setName("SUPER_ADMIN");

        when(roleRepository.findById(1L)).thenReturn(Optional.of(sampleRole));
        when(roleRepository.save(any(Role.class))).thenAnswer(i -> i.getArguments()[0]);

        RoleDTO result = roleService.updateRole(1L, updateDto);

        assertEquals("SUPER_ADMIN", result.getName());
    }

    @Test
    void updateRole_NotFound_ThrowsException() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                roleService.updateRole(1L, sampleDto));
    }

    @Test
    void deleteRole_Success() {
        when(roleRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> roleService.deleteRole(1L));
        verify(roleRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteRole_NotFound_ThrowsException() {
        when(roleRepository.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () ->
                roleService.deleteRole(1L));

        assertEquals("Role not found", exception.getMessage());
        verify(roleRepository, never()).deleteById(anyLong());
    }
}