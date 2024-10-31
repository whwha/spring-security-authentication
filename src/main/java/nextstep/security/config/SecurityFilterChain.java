package nextstep.security.config;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface SecurityFilterChain {

    boolean matches(HttpServletRequest request);

    List<Filter> getFilters();
}
