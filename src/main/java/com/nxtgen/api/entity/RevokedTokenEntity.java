package com.nxtgen.api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "NXTGEN_REVOKED_TOKENS")
public class RevokedTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TOKEN_JTI", nullable = false, unique = true)
    private String tokenJti;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "EXPIRES_AT", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "CRE_DATE")
    private LocalDateTime creDate;

    public RevokedTokenEntity() {
    }

    public RevokedTokenEntity(String tokenJti, String username, LocalDateTime expiresAt, LocalDateTime creDate) {
        this.tokenJti = tokenJti;
        this.username = username;
        this.expiresAt = expiresAt;
        this.creDate = creDate;
    }

    public Long getId() {
        return id;
    }

    public String getTokenJti() {
        return tokenJti;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getCreDate() {
        return creDate;
    }
}
