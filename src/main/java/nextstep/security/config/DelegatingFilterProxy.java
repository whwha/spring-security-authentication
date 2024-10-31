package nextstep.security.config;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import java.io.IOException;

public class DelegatingFilterProxy extends GenericFilterBean {
    private final Filter delegate;

    public DelegatingFilterProxy(Filter delegate) {
        this.delegate = delegate;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        delegate.doFilter(request, response, chain);
    }
}
