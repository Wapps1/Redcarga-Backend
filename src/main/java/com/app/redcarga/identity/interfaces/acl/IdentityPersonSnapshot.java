package com.app.redcarga.identity.interfaces.acl;

/**
 * Snapshot público que otros BC pueden consumir.
 * Solo campos necesarios: fullName + dni (docNumber).
 */
public record IdentityPersonSnapshot(Integer accountId, String fullName, String docNumber) {}