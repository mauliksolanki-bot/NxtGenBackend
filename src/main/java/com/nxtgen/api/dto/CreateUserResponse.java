package com.nxtgen.api.dto;

public class CreateUserResponse {

    private final Long id;
    private final String username;
    private final String emplNm;
    private final String emailAddress;
    private final String message;

    public CreateUserResponse(Long id, String username, String emplNm, String emailAddress, String message) {
        this.id = id;
        this.username = username;
        this.emplNm = emplNm;
        this.emailAddress = emailAddress;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmplNm() {
        return emplNm;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getMessage() {
        return message;
    }
}
