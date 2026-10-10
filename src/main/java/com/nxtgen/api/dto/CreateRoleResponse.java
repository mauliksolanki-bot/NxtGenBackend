package com.nxtgen.api.dto;

public class CreateRoleResponse {

    private final Long id;
    private final String roleName;
    private final String message;

    public CreateRoleResponse(Long id, String roleName, String message) {
        this.id = id;
        this.roleName = roleName;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getMessage() {
        return message;
    }
}
