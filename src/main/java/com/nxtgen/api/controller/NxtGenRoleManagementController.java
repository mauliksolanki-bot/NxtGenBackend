package com.nxtgen.api.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nxtgen.api.dto.CreateRoleRequest;
import com.nxtgen.api.dto.CreateRoleResponse;
import com.nxtgen.api.exception.RoleValidationException;
import com.nxtgen.api.service.RoleManagementService;

/**
 * Create Role workflow endpoint backed by the NXTGEN_ROLES table, metadata
 * for the form itself is served via the shared /api/formconfig endpoint.
 */
@RestController
@RequestMapping("/api")
public class NxtGenRoleManagementController {

    private final RoleManagementService roleManagementService;

    public NxtGenRoleManagementController(RoleManagementService roleManagementService) {
        this.roleManagementService = roleManagementService;
    }

    @PostMapping("/createnewrole")
    public CreateRoleResponse createNewRole(@Valid @RequestBody CreateRoleRequest request) {
        return roleManagementService.createRole(request);
    }

    @ExceptionHandler(RoleValidationException.class)
    public ResponseEntity<Map<String, Object>> handleRoleValidation(RoleValidationException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", exception.getMessage());
        body.put("errors", exception.getErrors());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
