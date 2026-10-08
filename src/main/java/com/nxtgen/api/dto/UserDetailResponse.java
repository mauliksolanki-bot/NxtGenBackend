package com.nxtgen.api.dto;

import java.util.List;

/**
 * Full detail payload used to populate the Edit User form when an existing
 * user record is opened from the Users grid.
 */
public class UserDetailResponse {

    private final Long id;
    private final Long userId;
    private final String firstName;
    private final String lastName;
    private final String emplNm;
    private final String username;
    private final String emailAddress;
    private final List<Long> roleIds;
    private final List<OptionResponse> groups;
    private final String isSuperAdmin;

    public UserDetailResponse(
            Long id,
            Long userId,
            String firstName,
            String lastName,
            String emplNm,
            String username,
            String emailAddress,
            List<Long> roleIds,
            List<OptionResponse> groups,
            String isSuperAdmin
    ) {
        this.id = id;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emplNm = emplNm;
        this.username = username;
        this.emailAddress = emailAddress;
        this.roleIds = roleIds;
        this.groups = groups;
        this.isSuperAdmin = isSuperAdmin;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmplNm() {
        return emplNm;
    }

    public String getUsername() {
        return username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public List<OptionResponse> getGroups() {
        return groups;
    }

    public String getIsSuperAdmin() {
        return isSuperAdmin;
    }
}
