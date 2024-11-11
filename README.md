# spring-security-authentication

# 3단계

## 1. SecurityFilterChain 적용

스프링 시큐리티는 기본적으로 필터를 활용한다. 기존에 구현한 인터셉터 기반의 인증을 필터 방식으로 전환한다.

- Interceptor 를 filter 로 구현
- 필터를 동작시킬 환경을 구축한다
    - 보안 필터를 서블릿 필터 체인에 등록하지 않고 아래 그림 참고해서 구성
    - DelegatingFilterProxy -> FilterChainProxy -> SecurityFilterChain
- 추가로 필요한 코드가 있는데 스프링 시큐리티 코드를 참고하여 작성하는 것을 권장한다. 스프링 시큐리티 필터 체인 공식 문서를 활용하여 구현해도 좋다. 다만 너무 똑같이 만들려고 하면 오히려 힘들 수 있으니
  주의한다.

![image](https://docs.spring.io/spring-security/reference/_images/servlet/architecture/securityfilterchain.png)

## 2. AuthenticationManager를 활용한 인증 추상화

스프링 시큐리티는 다양한 인증 방식을 제공하고 있으며, 이 방식들을 AuthenticationManager라는 형태를 활용하여 추상화해 놓았다. 기존에 구현한 인증 처리 로직을 추상화 하고 인증 방식에 따른 구현체를
구현한다. 아래 이미지에서 Authentication Filter 이후 (3번) 인증 과정에 집중하여 리팩터링한다.

- Authentication, AuthenticationManager, AuthenticationProvider 인터페이스를 작성하여 구조의 뼈대를 만든다.
- ProviderManager, DaoAuthenticationProvider를 구현하며 흐름을 제어하는 코드를 만든다.
- 그 외 필요한 객체(UsernamePasswordAuthenticationToken 등)를 구현하며 기능을 완성한 후 기존 필터에서 인증 로직을 분리한다.

![image](https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/ad3da2895e864a3baca9861dfdb99650)

## 3. SecurityContextHolder 적용

인증 성공 후 만든 Authentication(인증 정보 객체)을 세션으로 관리하고 있었는데, 스레드 로컬에 보관하도록 변경한다. 각 필터에서 스레드 로컬에 보관된 인증 정보에 접근을 할 수 있도록 한다.

- SecurityContext, SecurityContextHolder를 작성하여 Authentication 객체를 보관할 구조의 뼈대를 만든다.
- BasicAuthenticationFilter에서 SecurityContextHolder를 활용하여 Authentication 객체 보관하도록 한다.

![image](https://docs.spring.io/spring-security/reference/_images/servlet/authentication/architecture/securitycontextholder.png)

- 주요 클래스: SecurityContextHolder, SecurityContext

## 4. SecurityContextHolderFilter 구현

UsernamePasswordAuthenticationFilter는 로그인 성공 후 세션에 인증 정보(Authentication)를 보관하여 다음 요청에서 인증 정보를 활용한다. 세션에 담긴 인증 정보를
SecurityContextHolder로 옮기는 SecurityContextHolderFilter 필터를 구현하고, 세션 정보를 관리하는 SecurityContextRepository를 구현한다.

- 인증 정보를 보관하는 SecurityContextRepository를 구현한다. 실제 코드에서는 SecurityContextRepository는 인터페이스고,
  HttpSessionSecurityContextRepository가 구현체이다. 스프링 시큐리티에서는 세션 외 다른 방법으로 인증 정보를 유지할 수 있다.
- SecurityContextHolderFilter를 구현하여 SecurityContextRepository에 있는 인증 정보를 SecurityContextHolder로 옮겨오고 해당 필터를 적절한 순서에
  등록한다.
- 기능의 정상 동작 여부를 판단하기 위해 ✅ login_after_members 테스트를 활용한다.