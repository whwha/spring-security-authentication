package nextstep.security.authentication;

import nextstep.security.AuthenticationException;
import nextstep.security.UserDetails;
import nextstep.security.UserDetailsService;

import java.util.Objects;

public class DaoAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;

    public DaoAuthenticationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());
        if (!Objects.equals(userDetails.getPassword(), authentication.getCredentials().toString())) {
            throw new AuthenticationException();
        }
        return UsernamePasswordAuthenticationToken.authenticated(userDetails.getUsername(), userDetails.getPassword());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // isAssignableFrom() 메서드는 해당 클래스가 다른 클래스를 상속하거나 같은 클래스인지 확인
        // true 이면, UsernamePasswordAuthenticationToken 클래스는 OtherClass 클래스를 상속하거나 같은 클래스
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
