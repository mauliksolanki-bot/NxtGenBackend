package com.nxtgen.api.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nxtgen.api.constant.NxtGenCommonConstant;
import com.nxtgen.api.dto.ChangePasswordRequest;
import com.nxtgen.api.dto.LoginRequest;
import com.nxtgen.api.dto.LoginResponse;
import com.nxtgen.api.entity.RevokedTokenEntity;
import com.nxtgen.api.entity.UserAuditEntity;
import com.nxtgen.api.entity.UserMasterEntity;
import com.nxtgen.api.exception.AuthenticationFailedException;
import com.nxtgen.api.exception.UserValidationException;
import com.nxtgen.api.repository.RevokedTokenRepository;
import com.nxtgen.api.repository.UserAuditRepository;
import com.nxtgen.api.repository.UserMasterRepository;
import com.nxtgen.api.security.JwtTokenProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import static com.nxtgen.api.constant.NxtGenCommonConstant.*;

@Service
public class UserAuthenticationService {

    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid username or password.";
    private static final String MISSING_TOKEN_MESSAGE = "You are already signed out.";
    private static final String INVALID_TOKEN_MESSAGE = "Your session could not be verified.";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final String PASSWORD_CHANGED_ACTIVITY = "PASSWORD_CHANGED";

    private final UserMasterRepository userMasterRepository;
    private final UserAuditRepository userAuditRepository;
    private final RevokedTokenRepository revokedTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UserAuthenticationService(
            UserMasterRepository userMasterRepository,
            UserAuditRepository userAuditRepository,
            RevokedTokenRepository revokedTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userMasterRepository = userMasterRepository;
        this.userAuditRepository = userAuditRepository;
        this.revokedTokenRepository = revokedTokenRepository;
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
                ENABLED_FLAG.equalsIgnoreCase(user.getIsSupAdmin()),
                ENABLED_FLAG.equalsIgnoreCase(user.getPasswordResetRequired())
        );
    }

    public void changePassword(String authorizationHeader, ChangePasswordRequest request) {
        String token = extractBearerToken(authorizationHeader)
                .orElseThrow(() -> new AuthenticationFailedException(INVALID_TOKEN_MESSAGE));

        Claims claims = parseClaimsOrFail(token);
        String username = claims.getSubject();
        String jti = resolveJti(claims, token);

        if (claims.getExpiration().before(new java.util.Date()) || revokedTokenRepository.existsByTokenJti(jti)) {
            throw new AuthenticationFailedException(INVALID_TOKEN_MESSAGE);
        }

        UserMasterEntity user = userMasterRepository.findByUsername(username)
                .filter(this::isActive)
                .filter(this::isNotLocked)
                .orElseThrow(() -> new AuthenticationFailedException(INVALID_TOKEN_MESSAGE));

        String newPassword = Optional.ofNullable(request.getNewPassword()).orElse("");
        String confirmPassword = Optional.ofNullable(request.getConfirmPassword()).orElse("");
        Map<String, String> errors = new HashMap<>();

        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            errors.put("newPassword", "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.");
        } else if (DEFAULT_USER_PASSWORD.equals(newPassword) || isPasswordValid(newPassword, user)) {
            errors.put("newPassword", "New password must be different from your current password.");
        }

        if (!newPassword.equals(confirmPassword)) {
            errors.put("confirmPassword", "Passwords do not match.");
        }

        if (!errors.isEmpty()) {
            throw new UserValidationException("Please correct the highlighted fields.", errors);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetRequired("N");
        userMasterRepository.save(user);
        recordAudit(username, PASSWORD_CHANGED_ACTIVITY);
    }

    public void logout(String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader)
                .orElseThrow(() -> new AuthenticationFailedException(MISSING_TOKEN_MESSAGE));

        Claims claims = parseClaimsOrFail(token);
        String username = claims.getSubject();
        String jti = resolveJti(claims, token);
        LocalDateTime expiresAt = toLocalDateTime(claims.getExpiration());

        if (!revokedTokenRepository.existsByTokenJti(jti)) {
            revokedTokenRepository.save(new RevokedTokenEntity(jti, username, expiresAt, LocalDateTime.now()));
        }

        recordAudit(username, LOGOUT_SUCCESS_ACTIVITY);
    }

    /**
     * Tokens issued before the {@code jti} claim was introduced do not carry one.
     * For those, derive a stable fallback identifier from the token itself so
     * logout still works and the same token always maps to the same revocation row.
     */
    private String resolveJti(Claims claims, String token) {
        return Optional.ofNullable(claims.getId())
                .filter(id -> !id.isBlank())
                .orElseGet(() -> UUID.nameUUIDFromBytes(token.getBytes(StandardCharsets.UTF_8)).toString());
    }

    private Optional<String> extractBearerToken(String authorizationHeader) {
        return Optional.ofNullable(authorizationHeader)
                .filter(header -> header.startsWith(BEARER_PREFIX))
                .map(header -> header.substring(BEARER_PREFIX.length()).trim())
                .filter(token -> !token.isEmpty());
    }

    private Claims parseClaimsOrFail(String token) {
        try {
            return jwtTokenProvider.parseClaims(token);
        } catch (JwtException | IllegalArgumentException exception) {
            throw new AuthenticationFailedException(INVALID_TOKEN_MESSAGE);
        }
    }

    private LocalDateTime toLocalDateTime(java.util.Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
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
