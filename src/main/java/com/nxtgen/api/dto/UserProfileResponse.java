package com.nxtgen.api.dto;

import java.util.List;

public class UserProfileResponse {

    private final String username;
    private final List<String> roles;
    private final List<String> groups;

    public UserProfileResponse(String username, List<String> roles, List<String> groups) {
        this.username = username;
        this.roles = roles;
        this.groups = groups;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getGroups() {
        return groups;
    }
}
