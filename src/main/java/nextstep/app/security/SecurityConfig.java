package nextstep.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeRequests().anyRequest().authenticated();
        http.httpBasic();
        http
                .formLogin((form) -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .authorizeHttpRequests(
                        (requests) -> requests
//                                .antMatchers("/member/**").hasAuthority("MEMBER")
//                                .anyRequest().authenticated()
//                                .antMatchers("/", "/login").permitAll()
//                                .anyRequest().authenticated()
                                .anyRequest().permitAll()
                )

                .logout(LogoutConfigurer::permitAll)
                .authenticationProvider(authenticationProvider);

        return http.build();
    }


//    @Bean
//    public UserDetailsService userDetailsService() {
//        var userDetailsService = new InMemoryUserDetailsManager();
//        UserDetails user = User.withUsername("john")
//                .password("12345")
//                .roles("USER")
//                .build();
//        userDetailsService.createUser(user);
//        return userDetailsService;
//    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
//    }
//
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
}
