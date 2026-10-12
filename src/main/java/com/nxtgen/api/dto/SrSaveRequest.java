package com.nxtgen.api.dto;

public record SrSaveRequest(
        String requestType,
        String requestedFor,
        String dueDate,
        String quantity,
        String ciItem,
        String estimatedDelivery,
        String title,
        String description,
        String workNotes,
        String categorySlug,
        String subcategorySlug,
        String watchList,
        Boolean backordered,
        String orderGuide
) {
}
