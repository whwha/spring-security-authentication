package nextstep.security;

import nextstep.app.ui.AuthenticationException;

public interface UserDetailsService {
    UserDetails loadUserByUsername(String username) throws AuthenticationException;
}
