package com.nxtgen.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

@Entity
@Table(name = "NXTGEN_USERS_MASTER")
public class UserMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "FIRST_NM", nullable = false)
    private String firstNm;

    @Column(name = "LAST_NM", nullable = false)
    private String lastNm;

    @Column(name = "EMPL_NM", nullable = false)
    private String emplNm;

    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "EMAIL_ADDRESS", nullable = false)
    private String emailAddress;

    @Column(name = "ACTV_FLAG")
    private String actvFlag;

    @Column(name = "IS_LOCKED")
    private String isLocked;

    @Column(name = "IS_SUP_ADMIN")
    private String isSupAdmin;

    public Long getId() {
        return id;
    }

    public String getFirstNm() {
        return firstNm;
    }

    public String getLastNm() {
        return lastNm;
    }

    public String getEmplNm() {
        return emplNm;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getActvFlag() {
        return actvFlag;
    }

    public String getIsLocked() {
        return isLocked;
    }

    public String getIsSupAdmin() {
        return isSupAdmin;
    }
}
