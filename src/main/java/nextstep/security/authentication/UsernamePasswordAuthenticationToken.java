package nextstep.security.authentication;

public class UsernamePasswordAuthenticationToken implements Authentication {
    private final Object principal;
    private final Object credentials;
    private final boolean isAuthenticated;

    public UsernamePasswordAuthenticationToken(Object principal, Object credentials, boolean isAuthenticated) {
        this.principal = principal;
        this.credentials = credentials;
        this.isAuthenticated = isAuthenticated;
    }

    public static UsernamePasswordAuthenticationToken unauthenticated(String principal, Object credentials) {
        return new UsernamePasswordAuthenticationToken(principal, credentials, false);
    }

    public static UsernamePasswordAuthenticationToken authenticated(String principal, Object credentials) {
        return new UsernamePasswordAuthenticationToken(principal, credentials, true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }
}
