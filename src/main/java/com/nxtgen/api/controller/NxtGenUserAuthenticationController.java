package com.nxtgen.api.controller;

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

import com.nxtgen.api.dto.LoginRequest;
import com.nxtgen.api.dto.LoginResponse;
import com.nxtgen.api.exception.AuthenticationFailedException;
import com.nxtgen.api.service.UserAuthenticationService;

@RestController
@RequestMapping("/api")
public class NxtGenUserAuthenticationController {

    private final UserAuthenticationService userAuthenticationService;

    public NxtGenUserAuthenticationController(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
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

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationFailed(AuthenticationFailedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", exception.getMessage()));
    }
}
