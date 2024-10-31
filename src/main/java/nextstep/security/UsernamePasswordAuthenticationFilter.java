package nextstep.security;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class UsernamePasswordAuthenticationFilter extends GenericFilterBean {
    public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";
    private static final String DEFAULT_REQUEST_URI = "/login";

    private final UserDetailsService userDetailsService;

    public UsernamePasswordAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!DEFAULT_REQUEST_URI.equals(((HttpServletRequest) request).getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Map<String, String[]> parameterMap = request.getParameterMap();
            String username = parameterMap.get("username")[0];
            String password = parameterMap.get("password")[0];

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!Objects.equals(userDetails.getPassword(), password)) {
                throw new AuthenticationException();
            }

            HttpSession session = ((HttpServletRequest) request).getSession();
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, userDetails);

        } catch (
                AuthenticationException e) {
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
