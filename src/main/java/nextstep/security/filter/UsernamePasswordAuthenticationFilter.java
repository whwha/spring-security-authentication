package nextstep.security.filter;

public class UsernamePasswordAuthenticationFilter {
    /*
    1. SecurityFilterChain 적용
        - Interceptor 를 filter 로 구현
        - 필터를 동작시킬 환경을 구축한다
            - 보안 필터를 서블릿 필터 체인에 등록하지 않고 아래 그림 참고해서 구성
            - DelegatingFilterProxy -> FilterChainProxy -> SecurityFilterChain
    2. AuthenticationManager를 활용한 인증 추상화
        스프링 시큐리티는 다양한 인증 방식을 제공하고 있으며, 이 방식들을 AuthenticationManager라는 형태를 활용하여 추상화해 놓았다. 기존에 구현한 인증 처리 로직을 추상화 하고 인증 방식에 따른 구현체를 구현한다. 아래 이미지에서 Authentication Filter 이후 (3번) 인증 과정에 집중하여 리팩터링한다.
        - Authentication, AuthenticationManager, AuthenticationProvider 인터페이스를 작성하여 구조의 뼈대를 만든다.
        - ProviderManager, DaoAuthenticationProvider를 구현하며 흐름을 제어하는 코드를 만든다.
        - 그 외 필요한 객체(UsernamePasswordAuthenticationToken 등)를 구현하며 기능을 완성한 후 기존 필터에서 인증 로직을 분리한다.
     */

}
