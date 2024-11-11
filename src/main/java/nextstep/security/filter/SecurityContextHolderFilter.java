package nextstep.security.filter;

import nextstep.security.context.HttpSessionSecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SecurityContextHolderFilter extends GenericFilterBean {
    private final HttpSessionSecurityContextRepository httpSessionSecurityContextRepository = new HttpSessionSecurityContextRepository();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        SecurityContext securityContext = httpSessionSecurityContextRepository.loadContext((HttpServletRequest) servletRequest);
        SecurityContextHolder.setContext(securityContext);

        filterChain.doFilter(servletRequest, servletResponse);

        SecurityContextHolder.clearContext();
    }
}
