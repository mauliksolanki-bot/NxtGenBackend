package com.nxtgen.api.dto;

public class CreateGroupResponse {

    private final Long id;
    private final String grpName;
    private final String message;

    public CreateGroupResponse(Long id, String grpName, String message) {
        this.id = id;
        this.grpName = grpName;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public String getGrpName() {
        return grpName;
    }

    public String getMessage() {
        return message;
    }
}
