import java.util.Arrays;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MigrationValidator implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(MigrationValidator.class);

    private final Flyway flyway;
    private final MeterRegistry meterRegistry;

    public MigrationValidator(Flyway flyway, MeterRegistry meterRegistry) {
        this.flyway = flyway;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        MigrationInfo[] all = flyway.info().all();

        long applied = Arrays.stream(all)
            .filter(m -> m.getState() == MigrationState.SUCCESS).count();

        long pending = Arrays.stream(all)
            .filter(m -> m.getState() == MigrationState.PENDING).count();

        long failed = Arrays.stream(all)
            .filter(m -> m.getState() == MigrationState.FAILED).count();

        // Expose to Prometheus/Grafana
        meterRegistry.gauge("db.migrations.applied", applied);
        meterRegistry.gauge("db.migrations.pending", pending);
        meterRegistry.gauge("db.migrations.failed", failed);

        log.info("=== DB Migration Status: applied={}, pending={}, failed={} ===",
            applied, pending, failed);

        if (failed > 0) {
            // In a bank, a bad DB state = hard shutdown
            log.error("CRITICAL: Failed migrations detected. Shutting down.");
            SpringApplication.exit(event.getApplicationContext(), () -> 1);
        }
    }
}