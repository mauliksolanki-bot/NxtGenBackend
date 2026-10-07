package com.nxtgen.api.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nxtgen.api.constant.NxtGenCommonConstant;
import com.nxtgen.api.dto.LoginRequest;
import com.nxtgen.api.dto.LoginResponse;
import com.nxtgen.api.entity.UserAuditEntity;
import com.nxtgen.api.entity.UserMasterEntity;
import com.nxtgen.api.exception.AuthenticationFailedException;
import com.nxtgen.api.repository.UserAuditRepository;
import com.nxtgen.api.repository.UserMasterRepository;
import com.nxtgen.api.security.JwtTokenProvider;

import static com.nxtgen.api.constant.NxtGenCommonConstant.*;

@Service
public class UserAuthenticationService {

    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid username or password.";

    private final UserMasterRepository userMasterRepository;
    private final UserAuditRepository userAuditRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserAuthenticationService(
            UserMasterRepository userMasterRepository,
            UserAuditRepository userAuditRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userMasterRepository = userMasterRepository;
        this.userAuditRepository = userAuditRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        UserMasterEntity user = userMasterRepository.findByUsername(loginRequest.getUsername())
                .filter(candidate -> isPasswordValid(loginRequest.getPassword(), candidate))
                .filter(this::isActive)
                .filter(this::isNotLocked)
                .map(validUser -> {
                    recordAudit(loginRequest.getUsername(), LOGIN_SUCCESS_ACTIVITY);
                    return validUser;
                })
                .orElseGet(() -> {
                    recordAudit(loginRequest.getUsername(), LOGIN_FAILED_ACTIVITY);
                    throw new AuthenticationFailedException(INVALID_CREDENTIALS_MESSAGE);
                });

        long expirationMs = jwtTokenProvider.resolveExpirationMs(loginRequest.isRememberMe());
        String token = jwtTokenProvider.generateToken(user.getUsername(), expirationMs);

        return new LoginResponse(
                token,
                "Bearer",
                expirationMs,
                user.getUsername(),
                user.getEmplNm(),
                ENABLED_FLAG.equalsIgnoreCase(user.getIsSupAdmin())
        );
    }

    private boolean isPasswordValid(String rawPassword, UserMasterEntity user) {
        return Optional.ofNullable(user.getPassword())
                .map(encodedPassword -> passwordEncoder.matches(rawPassword, encodedPassword))
                .orElse(false);
    }

    private boolean isActive(UserMasterEntity user) {
        return ACTIVE_FLAG.equalsIgnoreCase(user.getActvFlag());
    }

    private boolean isNotLocked(UserMasterEntity user) {
        return !LOCKED_FLAG.equalsIgnoreCase(user.getIsLocked());
    }

    private void recordAudit(String username, String activity) {
        userAuditRepository.save(new UserAuditEntity(username, activity, username, LocalDateTime.now()));
    }
}
