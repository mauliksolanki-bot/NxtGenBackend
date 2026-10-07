package com.nxtgen.api.entity;

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

    @Column(name = "ACCESS_LEVEL")
    private String accessLevel;

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

    public String getAccessLevel() {
        return accessLevel;
    }
}
