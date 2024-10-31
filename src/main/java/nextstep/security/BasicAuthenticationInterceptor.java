package nextstep.security;

import org.springframework.http.HttpHeaders;
import org.springframework.util.Base64Utils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class BasicAuthenticationInterceptor implements HandlerInterceptor {
    private final UserDetailsService userDetailsService;

    public BasicAuthenticationInterceptor(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            checkAuthentication(request);
            return true;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    private void checkAuthentication(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        String[] split = authorization.split(" ");
        String type = split[0];
        String credential = split[1];

        if (!"Basic".equalsIgnoreCase(type)) {
            throw new AuthenticationException();
        }

        String decodedCredential = new String(Base64Utils.decodeFromString(credential));
        String[] emailAndPassword = decodedCredential.split(":");

        String email = emailAndPassword[0];
        String password = emailAndPassword[1];

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (!Objects.equals(userDetails.getPassword(), password)) {
            throw new AuthenticationException();
        }
    }

}
