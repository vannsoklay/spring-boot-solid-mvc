package demo.web_api.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            try {
                Authentication auth = SecurityContextHolder
                        .getContext()
                        .getAuthentication();

                if (auth == null
                        || !auth.isAuthenticated()
                        || auth.getPrincipal() == null
                        || auth.getPrincipal().equals("anonymousUser")) {
                    return Optional.of("SYSTEM");
                }

                return Optional.of(auth.getName());

            } catch (Exception e) {
                return Optional.of("SYSTEM");
            }
        };
    }
}