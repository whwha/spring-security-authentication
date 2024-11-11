package nextstep.security.filter;

import nextstep.security.AuthenticationException;
import nextstep.security.UserDetailsService;
import nextstep.security.authentication.*;
import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class UsernamePasswordAuthenticationFilter extends GenericFilterBean {
    private static final String DEFAULT_REQUEST_URI = "/login";

    private final AuthenticationManager authenticationManager;
    private final HttpSessionSecurityContextRepository httpSessionSecurityContextRepository = new HttpSessionSecurityContextRepository();

    public UsernamePasswordAuthenticationFilter(UserDetailsService userDetailsService) {
        this.authenticationManager = new ProviderManager(
                List.of(new DaoAuthenticationProvider(userDetailsService))
        );
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!DEFAULT_REQUEST_URI.equals(((HttpServletRequest) servletRequest).getRequestURI())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        try {
            Authentication authentication = convert(servletRequest);
            if (authentication == null) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            Authentication authenticate = this.authenticationManager.authenticate(authentication);
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authenticate);
            SecurityContextHolder.setContext(securityContext);

            httpSessionSecurityContextRepository.saveContext(securityContext, (HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
        } catch (AuthenticationException e) {
            ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private Authentication convert(ServletRequest servletRequest) {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            Map<String, String[]> parameterMap = request.getParameterMap();
            String username = parameterMap.get("username")[0];
            String password = parameterMap.get("password")[0];
            return UsernamePasswordAuthenticationToken.unauthenticated(username, password);

        } catch (AuthenticationException e) {
            return null;
        }
    }

}
