package com.app.redcarga.shared.infrastructure.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class StartupWarmup {
    private static final Logger log = LoggerFactory.getLogger(StartupWarmup.class);
    private final JdbcTemplate jdbc;

    public StartupWarmup(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void warm() {
        try {
            long t0 = System.currentTimeMillis();
            jdbc.queryForObject("select 1", Integer.class);
            // intenta leer la tabla de membresía para calentar el mismo path que usa el interceptor
            try {
                jdbc.queryForList("select 1 from providers.company_members limit 1");
            } catch (Exception e) {
                // si la tabla no existe en algunos entornos, no interrumpimos el arranque
                log.debug("[Warmup] tabla company_members no disponible: {}", e.toString());
            }
            log.info("[Warmup] DB ok in {}ms", System.currentTimeMillis() - t0);
        } catch (Exception e) {
            log.warn("[Warmup] skipped: {}", e.toString());
        }
    }
}
