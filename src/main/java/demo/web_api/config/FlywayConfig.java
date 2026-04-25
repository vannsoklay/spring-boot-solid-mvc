package demo.web_api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {
 
    private static final Logger log = LoggerFactory.getLogger(FlywayConfig.class);
 
    // Using Spring Boot's default Flyway migration strategy
}