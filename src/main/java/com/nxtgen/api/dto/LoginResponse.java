package com.nxtgen.api.dto;

public class LoginResponse {

    private final String token;
    private final String tokenType;
    private final long expiresInMs;
    private final String username;
    private final String displayName;
    private final boolean supAdmin;
    private final boolean passwordResetRequired;

    public LoginResponse(
            String token,
            String tokenType,
            long expiresInMs,
            String username,
            String displayName,
            boolean supAdmin,
            boolean passwordResetRequired
    ) {
        this.passwordResetRequired = passwordResetRequired;
        this.token = token;
        this.tokenType = tokenType;
        this.expiresInMs = expiresInMs;
        this.username = username;
        this.displayName = displayName;
        this.supAdmin = supAdmin;
    }

    public String getToken() {
        return token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresInMs() {
        return expiresInMs;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isSupAdmin() {
        return supAdmin;
    }

    public boolean isPasswordResetRequired() {
        return passwordResetRequired;
    }
}
