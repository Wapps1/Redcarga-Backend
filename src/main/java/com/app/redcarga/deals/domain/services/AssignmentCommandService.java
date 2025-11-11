package com.app.redcarga.deals.domain.services;

public interface AssignmentCommandService {
    void assignToQuote(Integer quoteId, Integer driverId, Integer vehicleId, Integer actorAccountId);
    void unassignFromQuote(Integer quoteId, Integer actorAccountId);
}
