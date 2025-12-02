package com.app.redcarga.deals.interfaces.rest.responses;

public record ChatListItemDto(
        Integer quoteId,
        Integer otherUserId,
        Integer otherCompanyId,
        String otherCompanyLegalName,
        String otherCompanyTradeName,
        String otherPersonFullName,
        Integer unreadCount
) {}
