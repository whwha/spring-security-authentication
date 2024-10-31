package nextstep.security.authentication;

public interface Authentication {

    // Collection<? extends GrantedAuthority> getAuthorities();

    Object getCredentials();

    Object getPrincipal();

    boolean isAuthenticated();
}
