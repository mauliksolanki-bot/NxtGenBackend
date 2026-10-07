package com.nxtgen.api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "NXTGEN_NAV_MENU")
public class NavMenuEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "MENU_NAME", nullable = false)
    private String menuName;

    @Column(name = "MENU_CODE", nullable = false)
    private String menuCode;

    @Column(name = "MENU_DISPLAY_NAME")
    private String menuDisplayName;

    @Column(name = "MENU_URL")
    private String menuUrl;

    @Column(name = "IS_ENABLED")
    private String isEnabled;

    @Column(name = "MENU_TYPE")
    private String menuType;

    @Column(name = "PARENT_MENU_ID")
    private Long parentMenuId;

    @Column(name = "CRE_BY")
    private String creBy;

    @Column(name = "CRE_DATE")
    private LocalDateTime creDate;

    public Long getId() {
        return id;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public String getMenuDisplayName() {
        return menuDisplayName;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public String getIsEnabled() {
        return isEnabled;
    }

    public String getMenuType() {
        return menuType;
    }

    public Long getParentMenuId() {
        return parentMenuId;
    }

    public String getCreBy() {
        return creBy;
    }

    public LocalDateTime getCreDate() {
        return creDate;
    }
}
