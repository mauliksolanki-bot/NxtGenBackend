package com.nxtgen.api.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nxtgen.api.dto.ChangePasswordRequest;
import com.nxtgen.api.dto.LoginRequest;
import com.nxtgen.api.dto.LoginResponse;
import com.nxtgen.api.dto.UserProfileResponse;
import com.nxtgen.api.service.UserProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import com.nxtgen.api.exception.AuthenticationFailedException;
import com.nxtgen.api.exception.UserValidationException;
import com.nxtgen.api.service.UserAuthenticationService;

@RestController
@RequestMapping("/api")
public class NxtGenUserAuthenticationController {

    private final UserAuthenticationService userAuthenticationService;
    private final UserProfileService userProfileService;

    public NxtGenUserAuthenticationController(
            UserAuthenticationService userAuthenticationService,
            UserProfileService userProfileService
    ) {
        this.userAuthenticationService = userAuthenticationService;
        this.userProfileService = userProfileService;
    }

    @GetMapping("/myprofile")
    public UserProfileResponse myProfile(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        return userProfileService.getProfile(authorizationHeader);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        return userAuthenticationService.login(loginRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        userAuthenticationService.logout(authorizationHeader);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }

    @PostMapping("/changepassword")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody ChangePasswordRequest request
    ) {
        userAuthenticationService.changePassword(authorizationHeader, request);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully."));
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<Map<String, Object>> handleUserValidation(UserValidationException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", exception.getMessage());
        body.put("errors", exception.getErrors());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationFailed(AuthenticationFailedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", exception.getMessage()));
    }
}
