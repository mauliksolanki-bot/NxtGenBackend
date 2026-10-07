package com.nxtgen.api.dto;

import java.util.ArrayList;
import java.util.List;

public class NavMenuResponse {

    private final Long id;
    private final String menuCode;
    private final String menuName;
    private final String menuDisplayName;
    private final String menuUrl;
    private final String menuType;
    private final List<NavMenuResponse> children = new ArrayList<>();

    public NavMenuResponse(
            Long id,
            String menuCode,
            String menuName,
            String menuDisplayName,
            String menuUrl,
            String menuType
    ) {
        this.id = id;
        this.menuCode = menuCode;
        this.menuName = menuName;
        this.menuDisplayName = menuDisplayName;
        this.menuUrl = menuUrl;
        this.menuType = menuType;
    }

    public Long getId() {
        return id;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getMenuDisplayName() {
        return menuDisplayName;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public String getMenuType() {
        return menuType;
    }

    public List<NavMenuResponse> getChildren() {
        return children;
    }
}
