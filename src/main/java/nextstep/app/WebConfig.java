package nextstep.app;

import nextstep.app.domain.MemberRepository;
import nextstep.app.interceptor.BasicAuthenticationInterceptor;
import nextstep.app.interceptor.UsernamePasswordAuthenticationInterceptor;
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
        registry.addInterceptor(new UsernamePasswordAuthenticationInterceptor(memberRepository)).addPathPatterns("/login");
        registry.addInterceptor(new BasicAuthenticationInterceptor(memberRepository)).addPathPatterns("/members");
    }
}
