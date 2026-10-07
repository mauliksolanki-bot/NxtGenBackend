package com.nxtgen.api.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nxtgen.api.dto.CreateUserRequest;
import com.nxtgen.api.dto.CreateUserResponse;
import com.nxtgen.api.dto.FormConfigResponse;
import com.nxtgen.api.dto.ValidationResponse;
import com.nxtgen.api.exception.FormNotFoundException;
import com.nxtgen.api.exception.UserNotFoundException;
import com.nxtgen.api.exception.UserValidationException;
import com.nxtgen.api.service.FormConfigService;
import com.nxtgen.api.service.UserManagementService;

/**
 * Metadata-driven form config endpoint plus the Create User workflow
 * endpoints (validate then create), backed by the NXTGEN_FORM /
 * NXTGEN_FORM_FIELDS configuration tables.
 */
@RestController
@RequestMapping("/api")
public class NxtGenUserManagementController {

    private final FormConfigService formConfigService;
    private final UserManagementService userManagementService;

    public NxtGenUserManagementController(FormConfigService formConfigService, UserManagementService userManagementService) {
        this.formConfigService = formConfigService;
        this.userManagementService = userManagementService;
    }

    @GetMapping("/formconfig")
    public FormConfigResponse getFormConfig(@RequestParam String formName) {
        return formConfigService.getFormConfig(formName);
    }

    @PostMapping("/validateuserdata")
    public ValidationResponse validateUserData(@RequestBody CreateUserRequest request) {
        return userManagementService.validateUserData(request);
    }

    @PostMapping("/createnewuser")
    public CreateUserResponse createNewUser(@Valid @RequestBody CreateUserRequest request) {
        return userManagementService.createUser(request);
    }

    @PostMapping("/deleteuser")
    public ResponseEntity<Map<String, String>> deleteUser(@RequestParam Long id) {
        userManagementService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully."));
    }

    @ExceptionHandler(FormNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleFormNotFound(FormNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<Map<String, Object>> handleUserValidation(UserValidationException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", exception.getMessage());
        body.put("errors", exception.getErrors());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
