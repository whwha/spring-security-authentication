package nextstep.security;

public interface UserDetailsService {
    UserDetails loadUserByUsername(String username);
}
