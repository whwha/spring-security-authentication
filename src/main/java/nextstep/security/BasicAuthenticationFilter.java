package nextstep.security;

import org.springframework.http.HttpHeaders;
import org.springframework.util.Base64Utils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

public class BasicAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;

    public BasicAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            checkAuthentication(request);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
