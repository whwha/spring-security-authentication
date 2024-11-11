package nextstep.security.filter;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import java.io.IOException;

public class DelegatingFilterProxy extends GenericFilterBean {

    private final Filter delegate;

    public DelegatingFilterProxy(Filter delegate) {
        this.delegate = delegate;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        delegate.doFilter(servletRequest, servletResponse, filterChain);
    }
}
