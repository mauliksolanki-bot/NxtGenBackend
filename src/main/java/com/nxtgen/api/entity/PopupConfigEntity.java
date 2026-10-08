package com.nxtgen.api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "NXTGEN_POPUP_CONFIG")
public class PopupConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "POPUP_NAME", nullable = false)
    private String popupName;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "DISPLAY_MSG", nullable = false)
    private String displayMsg;

    @Column(name = "IS_ENABLED")
    private String isEnabled;

    @Column(name = "CRE_BY")
    private String creBy;

    @Column(name = "CRE_DATE")
    private LocalDateTime creDate;

    public Long getId() {
        return id;
    }

    public String getPopupName() {
        return popupName;
    }

    public String getTitle() {
        return title;
    }

    public String getDisplayMsg() {
        return displayMsg;
    }

    public String getIsEnabled() {
        return isEnabled;
    }

    public String getCreBy() {
        return creBy;
    }

    public LocalDateTime getCreDate() {
        return creDate;
    }
}
