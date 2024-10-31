package nextstep.security.config;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class DefaultSecurityFilterChain implements SecurityFilterChain {

    private final List<Filter> filters;

    public DefaultSecurityFilterChain(List<Filter> filters) {
        this.filters = filters;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return true;
    }

    @Override
    public List<Filter> getFilters() {
        return filters;
    }
}
