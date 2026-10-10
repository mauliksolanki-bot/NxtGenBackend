package com.nxtgen.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Warehouse-style bulk demo dataset (NXTGEN_USERS_MASTER_DATA_WH), used to
 * look up and auto-populate Create User form fields by User ID.
 */
@Entity
@Table(name = "NXTGEN_USERS_MASTER_DATA_WH")
public class UsersMasterDataWhEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "FIRST_NM", nullable = false)
    private String firstNm;

    @Column(name = "LAST_NM", nullable = false)
    private String lastNm;

    @Column(name = "EMAIL_ADDRESS", nullable = false)
    private String emailAddress;

    @Column(name = "IS_ACTIVE")
    private String isActive;

    public UsersMasterDataWhEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getFirstNm() {
        return firstNm;
    }

    public String getLastNm() {
        return lastNm;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getIsActive() {
        return isActive;
    }
}
