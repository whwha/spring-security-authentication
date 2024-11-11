package nextstep.app;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.security.AuthenticationException;
import nextstep.security.UserDetails;
import nextstep.security.UserDetailsService;
import nextstep.security.filter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SecurityConfig {
    private final MemberRepository memberRepository;

    public SecurityConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

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
        return new DefaultSecurityFilterChain(List.of(
                new UsernamePasswordAuthenticationFilter(userDetailsService()),
                new BasicAuthenticationFilter(userDetailsService())
        ));
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Member member = memberRepository.findByEmail(username)
                    .orElseThrow(AuthenticationException::new);

            return new UserDetails() {
                @Override
                public String getUsername() {
                    return member.getName();
                }

                @Override
                public String getPassword() {
                    return member.getPassword();
                }
            };
        };
    }
}
