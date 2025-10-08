package com.app.redcarga.shared.infrastructure.persistence.flyway;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;

@Configuration
public class MultiFlywayConfig {

    // === IAM (debe correr primero) ===
    @Bean(name = "flywayIam")
    public Flyway flywayIam(DataSource ds) {
        Flyway flyway = Flyway.configure()
                .dataSource(ds)
                .schemas("iam")
                .table("flyway_history_iam")
                .locations("classpath:db/migration/iam")
                .createSchemas(true)         // <--- crea schema iam si falta
                .group(true)
                .cleanDisabled(true)
                .load();
        flyway.migrate();                    // <--- migra IAM aquí
        return flyway;
    }

    // === Identity (después de IAM) ===
    @Bean(name = "flywayIdentity")
    @DependsOn("flywayIam")                  // <--- CLAVE: Identity espera a IAM
    public Flyway flywayIdentity(DataSource ds) {
        Flyway flyway = Flyway.configure()
                .dataSource(ds)
                .schemas("identity")
                .table("flyway_history_identity")
                .locations("classpath:db/migration/identity")
                .createSchemas(true)
                .group(true)
                .cleanDisabled(true)
                .load();
        flyway.migrate();
        return flyway;
    }

    // === Providers (si depende de IAM, que espere a IAM; si depende de Identity, añade ambos) ===
    @Bean(name = "flywayProviders")
    @DependsOn("flywayIam")
    public Flyway flywayProviders(DataSource ds) {
        Flyway flyway = Flyway.configure()
                .dataSource(ds)
                .schemas("providers")
                .table("flyway_history_providers")
                .locations("classpath:db/migration/providers")
                .createSchemas(true)
                .group(true)
                .cleanDisabled(true)
                .load();
        flyway.migrate();
        return flyway;
    }

    // === Geo/Admin (ajusta dependencias si necesita IAM/Identity) ===
    @Bean(name = "flywayAdminGeo")
    @DependsOn("flywayIam")
    public Flyway flywayAdminGeo(DataSource ds) {
        Flyway flyway = Flyway.configure()
                .dataSource(ds)
                .schemas("geo")
                .table("flyway_history_geo")
                .locations("classpath:db/migration/geo")
                .createSchemas(true)
                .group(true)
                .cleanDisabled(true)
                .load();
        flyway.migrate();
        return flyway;
    }

    // === Planning (debe esperar a Providers porque tiene FK a providers.companies) ===
    @Bean(name = "flywayPlanning")
    @DependsOn({"flywayIam", "flywayProviders"})
    public Flyway flywayPlanning(DataSource ds) {
        Flyway flyway = Flyway.configure()
                .dataSource(ds)
                .schemas("planning")
                .table("flyway_history_planning")
                .locations("classpath:db/migration/planning")
                .createSchemas(true)
                .group(true)
                .cleanDisabled(true)
                .load();
        flyway.migrate();
        return flyway;
    }

    // === Fleet (depende de Providers por company_id) ===
    @Bean(name = "flywayFleet")
    @DependsOn({"flywayIam", "flywayProviders"})
    public Flyway flywayFleet(DataSource ds) {
        Flyway flyway = Flyway.configure()
                .dataSource(ds)
                .schemas("fleet")
                .table("flyway_history_fleet")
                .locations("classpath:db/migration/fleet")
                .createSchemas(true)
                .group(true)
                .cleanDisabled(true)
                .load();
        flyway.migrate();
        return flyway;
    }

    @Bean
    @Order(6)
    public Flyway flywayRequests(DataSource ds) {
        Flyway flyway = Flyway.configure()
                .dataSource(ds)
                .schemas("requests")
                .table("flyway_history_requests")
                .locations("classpath:db/migration/requests")
                .group(true)
                .cleanDisabled(true)
                // .baselineOnMigrate(true)
                .load();
        flyway.migrate();
        return flyway;
    }
}
