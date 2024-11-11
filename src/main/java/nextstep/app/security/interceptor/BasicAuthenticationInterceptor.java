package nextstep.app.security.interceptor;

import nextstep.app.security.UserDetails;
import nextstep.app.security.UserDetailsService;
import nextstep.app.ui.AuthenticationException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BasicAuthenticationInterceptor implements HandlerInterceptor {

    // 2. 인증 로직과 서비스 로직간 패키지 분리
    //      - 서비스 코드와 인증 코드 분리: 서비스 관련 코드는 app 패키지에 위치시키고, 인증 관련 코드는 security 패키지에 위치시킨다
    //      - 리펙터링 과정에서 패키지간 양방향 참조가 발생한다면 단방향 참조로 리펙터링

    public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";
    private final UserDetailsService userDetailsService;

    public BasicAuthenticationInterceptor(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String username;
        String password;

        try {
            String encodedToken = request.getHeader("Authorization").split(" ")[1];
            byte[] decode = Base64.getDecoder().decode(encodedToken);
            String token = new String(decode, StandardCharsets.UTF_8);
            username = token.split(":")[0];
            password = token.split(":")[1];
        } catch (Exception e) {
            throw new AuthenticationException();
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!userDetails.getPassword().equals(password)) {
            throw new AuthenticationException();
        }

        request.getSession().setAttribute(SPRING_SECURITY_CONTEXT_KEY, userDetails);
        return true;
    }
}
