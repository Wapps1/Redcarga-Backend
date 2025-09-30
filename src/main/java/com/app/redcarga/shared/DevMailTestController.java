package com.app.redcarga.shared;

import com.app.redcarga.shared.application.gateways.MailGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DevMailTestController {

    private static final Logger log = LoggerFactory.getLogger(DevMailTestController.class);
    private final MailGateway mail;

    public DevMailTestController(MailGateway mail) {
        this.mail = mail;
    }

    @GetMapping("/_dev/mail/test")
    public ResponseEntity<?> testMail(
            @RequestParam String to,
            @RequestParam(required = false, defaultValue = "SMTP Smoke Test") String subject
    ) {
        try {
            // Texto plano
            String plain = """
                    Hola,
                    Esto es una prueba de SMTP desde Spring Boot.
                    
                    Si ves este correo, la entrega multipart (texto+HTML) funciona.
                    """;

            // HTML simple (seguro para filtros): botón + enlace visible
            String html = """
                    <div style="font-family:Arial,sans-serif;line-height:1.5;">
                      <h2>Prueba de SMTP</h2>
                      <p>Hola. Esto es una prueba de envío con parte HTML.</p>
                      <p style="margin:16px 0;">
                        <a href="https://example.com"
                           style="display:inline-block;padding:10px 16px;text-decoration:none;border:1px solid #222;border-radius:6px;">
                          Botón de prueba
                        </a>
                      </p>
                      <p style="font-size:12px;color:#555;">
                        Si el botón no funciona, abre este enlace:<br>
                        <span>https://example.com</span>
                      </p>
                    </div>
                    """;

            log.info("[_dev] sending test email to {}", to);
            mail.send(to, subject, plain, html); // << nueva firma
            log.info("[_dev] mail send() invoked");
            return ResponseEntity.ok().body(java.util.Map.of(
                    "status", "ok",
                    "to", to,
                    "subject", subject
            ));
        } catch (Exception e) {
            log.error("[_dev] mail send failed", e);
            return ResponseEntity.internalServerError().body(java.util.Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

}
