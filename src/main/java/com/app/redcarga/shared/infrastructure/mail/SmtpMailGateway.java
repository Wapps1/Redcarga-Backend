package com.app.redcarga.shared.infrastructure.mail;

import com.app.redcarga.shared.application.gateways.MailGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class SmtpMailGateway implements MailGateway {
    private static final Logger log = LoggerFactory.getLogger(SmtpMailGateway.class);

    private final JavaMailSender sender;
    private final String from;

    public SmtpMailGateway(JavaMailSender sender, @Value("${spring.mail.from}") String from) {
        this.sender = sender;
        this.from = from;
    }

    @Override
    public void send(String to, String subject, String plainBody, String htmlBody) {
        try {
            var msg = sender.createMimeMessage();
            // MULTIPART_MODE_MIXED_RELATED te da alternative (texto+html)
            var helper = new MimeMessageHelper(msg, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(plainBody, htmlBody); // <-- texto + html
            log.info("[smtp] about to send to {}", to);
            sender.send(msg);
            log.info("[smtp] sent to {}", to);
        } catch (Exception e) {
            log.error("[smtp] send failed", e);
            throw new IllegalStateException("SMTP send failed: " + e.getMessage(), e);
        }
    }
}
