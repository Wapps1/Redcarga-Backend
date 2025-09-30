package com.app.redcarga.shared.infrastructure.persistence.flyway;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class MultiFlywayConfig {

    // === IAM ===
    @Bean
    @Order(1) // por si quieres controlar orden entre BCs
    public Flyway flywayIam(DataSource ds) {
        Flyway flyway = Flyway.configure()
                .dataSource(ds)
                .schemas("iam")
                .table("flyway_history_iam")                 // historial separado
                .locations("classpath:db/migration/iam")     // carpeta de IAM
                // .baselineOnMigrate(true)                  // activa solo si ese esquema ya tiene objetos previos
                .load();

        flyway.migrate(); // ejecuta migraciones de este BC
        return flyway;
    }

    @Bean
    @Order(2) // luego Identity
    public Flyway flywayIdentity(DataSource ds) {
        Flyway flyway = Flyway.configure()
                .dataSource(ds)
                .schemas("identity")
                .table("flyway_history_identity")
                .locations("classpath:db/migration/identity")
                //.baselineOnMigrate(true) // si 'identity' ya tenía tablas
                //.baselineVersion("1")
                //.validateOnMigrate(true)
                .group(true)
                .cleanDisabled(true)
                .load();
        flyway.migrate();
        return flyway;
    }
}
