package nextstep.app;

import nextstep.app.domain.Member;
import nextstep.app.domain.MemberRepository;
import nextstep.app.security.UserDetails;
import nextstep.app.security.UserDetailsService;
import nextstep.app.security.interceptor.BasicAuthenticationInterceptor;
import nextstep.app.security.interceptor.UsernamePasswordAuthenticationInterceptor;
import nextstep.app.ui.AuthenticationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final MemberRepository memberRepository;

    public WebConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UsernamePasswordAuthenticationInterceptor(userDetailsService())).addPathPatterns("/login");
        registry.addInterceptor(new BasicAuthenticationInterceptor(userDetailsService())).addPathPatterns("/members");
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
