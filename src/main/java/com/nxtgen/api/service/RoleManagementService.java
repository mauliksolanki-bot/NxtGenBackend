package com.nxtgen.api.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nxtgen.api.dto.CreateRoleRequest;
import com.nxtgen.api.dto.CreateRoleResponse;
import com.nxtgen.api.entity.RoleEntity;
import com.nxtgen.api.exception.RoleValidationException;
import com.nxtgen.api.repository.RoleRepository;

@Service
public class RoleManagementService {

    private static final String VALIDATION_FAILED_MESSAGE = "Please correct the highlighted fields.";

    private final RoleRepository roleRepository;

    public RoleManagementService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public CreateRoleResponse createRole(CreateRoleRequest request) {
        Map<String, String> errors = collectValidationErrors(request);

        if (!errors.isEmpty()) {
            throw new RoleValidationException(VALIDATION_FAILED_MESSAGE, errors);
        }

        String roleName = request.getRoleName().trim();
        String description = Optional.ofNullable(request.getDescription()).map(String::trim).orElse(null);

        RoleEntity savedRole = roleRepository.save(new RoleEntity(roleName, description, request.getIsActive()));

        return new CreateRoleResponse(savedRole.getId(), savedRole.getRoleName(), "Role created successfully.");
    }

    private Map<String, String> collectValidationErrors(CreateRoleRequest request) {
        Map<String, String> errors = new HashMap<>();

        String roleName = Optional.ofNullable(request.getRoleName()).map(String::trim).orElse("");

        if (roleName.isEmpty()) {
            errors.put("roleName", "Role name is required.");
        } else if (roleRepository.existsByRoleNameIgnoreCase(roleName)) {
            errors.put("roleName", "A role with this name already exists.");
        }

        if (!"Y".equalsIgnoreCase(request.getIsActive()) && !"N".equalsIgnoreCase(request.getIsActive())) {
            errors.put("isActive", "Please select Yes or No for Active.");
        }

        return errors;
    }
}
