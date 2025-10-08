// requests/application/internal/config/RequestsOutboxProperties.java
package com.app.redcarga.requests.application.internal.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter @Setter
@ConfigurationProperties(prefix = "requests.outbox.match")
public class RequestsOutboxProperties {
    /** Habilita el scheduler. */
    private boolean enabled = true;
    /** Tamaño del batch por corrida. */
    private int batchSize = 50;
    /** Tiempo de lease para evitar doble toma. */
    private Duration lease = Duration.ofSeconds(30);
    /** Backoff entre reintentos cuando falla. */
    private Duration backoff = Duration.ofSeconds(30);
    /** Intentos máximos antes de pasar a DEAD. */
    private int maxAttempts = 8;
    /** Frecuencia cron del scheduler. */
    private String cron = "*/5 * * * * *"; // cada 5s
    /** Disparo inmediato after-commit además del outbox (útil en dev). */
    private boolean alsoFireAfterCommit = false;
}
