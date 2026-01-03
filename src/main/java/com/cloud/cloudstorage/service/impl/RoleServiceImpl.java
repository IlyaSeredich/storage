package com.cloud.cloudstorage.service.impl;

import com.cloud.cloudstorage.model.Role;
import com.cloud.cloudstorage.repository.RoleRepository;
import com.cloud.cloudstorage.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public Role getDefaultRole() {
        String roleName = "ROLE_USER";
        Optional<Role> role = findByName(roleName);
        return role.orElseGet(() -> createDefaultRole(roleName));
    }

    private Role createDefaultRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        roleRepository.save(role);
        return role;
    }

    private Optional<Role> findByName(String name) {
        return roleRepository.findRoleByName(name);
    }
}