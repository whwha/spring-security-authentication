package nextstep.security.filter;

import nextstep.security.AuthenticationException;
import nextstep.security.UserDetails;
import nextstep.security.UserDetailsService;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class UsernamePasswordAuthenticationFilter extends GenericFilterBean {
    public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";
    private static final String DEFAULT_REQUEST_URI = "/login";

    private final UserDetailsService userDetailsService;

    public UsernamePasswordAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!DEFAULT_REQUEST_URI.equals(((HttpServletRequest) servletRequest).getRequestURI())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            Map<String, String[]> parameterMap = request.getParameterMap();
            String username = parameterMap.get("username")[0];
            String password = parameterMap.get("password")[0];

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!userDetails.getPassword().equals(password)) {
                throw new AuthenticationException();
            }
            request.getSession().setAttribute(SPRING_SECURITY_CONTEXT_KEY, userDetails);
        } catch (AuthenticationException e) {
            ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
