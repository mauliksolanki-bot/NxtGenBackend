package com.nxtgen.api.dto;

public class UserWhDetailResponse {

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String emplNm;
    private final String emailAddress;

    public UserWhDetailResponse(Long id, String firstName, String lastName, String emplNm, String emailAddress) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emplNm = emplNm;
        this.emailAddress = emailAddress;
    }

    public Long getId() {
        return id;
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

    public String getEmailAddress() {
        return emailAddress;
    }
}
