package nextstep.app;

import nextstep.security.filter.DefaultSecurityFilterChain;
import nextstep.security.filter.DelegatingFilterProxy;
import nextstep.security.filter.FilterChainProxy;
import nextstep.security.filter.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

import java.util.List;

public class SecurityConfig {
    @Bean
    public DelegatingFilterProxy delegatingFilterProxy() {
        return new DelegatingFilterProxy(filterChainProxy(List.of(securityFilterChain())));
    }

    @Bean
    public FilterChainProxy filterChainProxy(List<SecurityFilterChain> securityFilterChains) {
        return new FilterChainProxy(securityFilterChains);
    }

    @Bean
    public SecurityFilterChain securityFilterChain() {
        return new DefaultSecurityFilterChain(List.of());
    }
}
