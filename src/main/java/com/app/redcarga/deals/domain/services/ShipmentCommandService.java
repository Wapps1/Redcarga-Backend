package com.app.redcarga.deals.domain.services;

public interface ShipmentCommandService {
	void markShipmentReceived(Integer quoteId, Integer actorAccountId);
	void markShipmentSent(Integer quoteId, Integer actorAccountId);
}
