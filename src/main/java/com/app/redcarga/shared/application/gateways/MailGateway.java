package com.app.redcarga.shared.application.gateways;

public interface MailGateway {
    void send(String to, String subject, String plainBody, String htmlBody);
}