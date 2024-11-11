package nextstep.security.filter;

import nextstep.security.AuthenticationException;
import nextstep.security.UserDetailsService;
import nextstep.security.authentication.*;
import org.springframework.util.Base64Utils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class BasicAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationManager authenticationManager;

    public BasicAuthenticationFilter(UserDetailsService userDetailsService) {
        this.authenticationManager = new ProviderManager(
                List.of(new DaoAuthenticationProvider(userDetailsService))
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication authentication = checkAuthentication(request);
            authenticationManager.authenticate(authentication);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private Authentication checkAuthentication(HttpServletRequest request) {
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

        return UsernamePasswordAuthenticationToken.unauthenticated(email, password);
    }
}
