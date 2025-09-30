package com.app.redcarga.iam.domain.model.events;

public record AccountRegistered(Integer accountId, String email, String roleCode) { }