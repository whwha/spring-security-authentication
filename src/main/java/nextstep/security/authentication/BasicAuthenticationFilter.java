package nextstep.security.authentication;

import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class BasicAuthenticationFilter extends OncePerRequestFilter {
    public static final String AUTHENTICATION_SCHEME_BASIC = "Basic";

    private final AuthenticationManager authenticationManager;

    public BasicAuthenticationFilter(UserDetailsService userDetailsService) {
        this.authenticationManager = new ProviderManager(
                List.of(new DaoAuthenticationProvider(userDetailsService))
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            Authentication authentication = convert(request);
            if (authentication == null) {
                filterChain.doFilter(request, response);
                return;
            }

            Authentication authenticate = this.authenticationManager.authenticate(authentication);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticate);
            SecurityContextHolder.setContext(context);

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private Authentication convert(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null) {
            return null;
        }
        header = header.trim();
        if (!StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BASIC)) {
            return null;
        }
        if (header.equalsIgnoreCase(AUTHENTICATION_SCHEME_BASIC)) {
            throw new AuthenticationException();
        }

        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded = decode(base64Token);
        String token = new String(decoded, StandardCharsets.UTF_8);
        int delim = token.indexOf(":");
        if (delim == -1) {
            throw new AuthenticationException();
        }

        return UsernamePasswordAuthenticationToken
                .unauthenticated(token.substring(0, delim), token.substring(delim + 1));
    }

    private byte[] decode(byte[] base64Token) {
        try {
            return Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException ex) {
            throw new AuthenticationException();
        }
    }
}
