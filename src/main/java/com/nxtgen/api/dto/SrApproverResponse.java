package com.nxtgen.api.dto;

public record SrApproverResponse(Long id, String state, String approver, String comments,
        String createdAt, Integer level, boolean editable) {}
