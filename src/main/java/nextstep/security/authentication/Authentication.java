package nextstep.security.authentication;

public interface Authentication {
    Object getCredentials();

    Object getPrincipal();

    boolean isAuthenticated();
}
