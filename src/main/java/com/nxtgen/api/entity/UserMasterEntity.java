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

    @Column(name = "EMAIL_ADDRESS", nullable = false, unique = true)
    private String emailAddress;

    @Column(name = "ACTV_FLAG")
    private String actvFlag;

    @Column(name = "IS_LOCKED")
    private String isLocked;

    @Column(name = "IS_SUP_ADMIN")
    private String isSupAdmin;

    @Column(name = "SRC_USER_ID")
    private Long srcUserId;

    @Column(name = "PASSWORD_RESET_REQUIRED")
    private String passwordResetRequired;

    public UserMasterEntity() {
    }

    public UserMasterEntity(
            String firstNm,
            String lastNm,
            String emplNm,
            String username,
            String password,
            String emailAddress,
            String actvFlag,
            String isLocked,
            String isSupAdmin,
            Long srcUserId
    ) {
        this.firstNm = firstNm;
        this.lastNm = lastNm;
        this.emplNm = emplNm;
        this.username = username;
        this.password = password;
        this.emailAddress = emailAddress;
        this.actvFlag = actvFlag;
        this.isLocked = isLocked;
        this.isSupAdmin = isSupAdmin;
        this.srcUserId = srcUserId;
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

    public Long getSrcUserId() {
        return srcUserId;
    }

    public void setFirstNm(String firstNm) {
        this.firstNm = firstNm;
    }

    public void setLastNm(String lastNm) {
        this.lastNm = lastNm;
    }

    public void setEmplNm(String emplNm) {
        this.emplNm = emplNm;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setIsSupAdmin(String isSupAdmin) {
        this.isSupAdmin = isSupAdmin;
    }

    public String getPasswordResetRequired() {
        return passwordResetRequired;
    }

    public void setPasswordResetRequired(String passwordResetRequired) {
        this.passwordResetRequired = passwordResetRequired;
    }

    public void setActvFlag(String actvFlag) {
        this.actvFlag = actvFlag;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
