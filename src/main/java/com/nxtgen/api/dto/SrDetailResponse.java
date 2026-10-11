package com.nxtgen.api.dto;

public record SrDetailResponse(
        String srId,
        String reqId,
        String requestType,
        String requestedFor,
        String openedAt,
        String openedBy,
        String stage,
        String state,
        String dueDate,
        String quantity,
        String ciItem,
        String estimatedDelivery,
        String title,
        String description,
        String workNotes,
        boolean editable,
        String subcategory,
        String formName
) {
}
