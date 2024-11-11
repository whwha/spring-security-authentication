package nextstep.security.filter;

import nextstep.security.AuthenticationException;
import nextstep.security.UserDetails;
import nextstep.security.UserDetailsService;
import org.springframework.util.Base64Utils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BasicAuthenticationFilter extends OncePerRequestFilter {
    public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";
    private final UserDetailsService userDetailsService;

    public BasicAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            checkAuthentication(request);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void checkAuthentication(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

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
        if (!userDetails.getPassword().equals(password)) {
            throw new AuthenticationException();
        }
    }
}
