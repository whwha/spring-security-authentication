# spring-security-authentication

# 1단계

## 목표

- 아이디와 패스워드 기반 로그인 기능을 구현할 수 있다.
- Basic Auth 방식을 활용하여 사용자를 식별할 수 있다.

## 1. 아이디와 패스워드 기반 로그인 구현

- ✅ `LoginTest`의 모든 테스트가 통과해야 한다.
- POST /login 경로로 로그인 요청을 한다.
- 사용자가 입력한 아이디와 비밀번호를 확인하여 인증한다.
- 로그인 성공 시 Session을 사용하여 인증 정보를 저장한다.

## 2. Basic Auth 인증 구현

- ✅ `MemberTest`의 모든 테스트가 통과해야 한다.
- GET /members 요청 시 Member 목록을 조회한다.
- 단, Member로 등록되어있는 사용자만 가능하도록 한다.
- 이를 위해 HTTP Basic 인증을 사용하여 사용자를 식별한다.
- 요청의 Authorization 헤더에서 Basic 인증 정보를 추출하여 인증을 처리한다.
- 인증 성공 시 Session을 사용하여 인증 정보를 저장한다.

# 2단계

## 목표

- 중복 제거와 추상화를 활용하여 리팩터링을 할 수 있다.
- 재사용 가능한 패키지를 구성하고 패키지간 의존 관계를 개선할 수 있다.
- ✅ `LoginTest`와 `MemberTest` 클래스의 모든 테스트가 지속해서 통과해야 한다.

## 1. 인터셉터 분리

- HandlerInterceptor를 사용하여 인증 관련 로직을 Controller 클래스에서 분리한다.
    - 1단계에서 구현한 두 인증 방식(아이디 비밀번호 로그인 방식과 HTTP Basic 인증 방식) 모두 인터셉터에서 처리되도록 구현한다.
    - 가급적이면 하나의 인터셉터는 하나의 작업만 수행하도록 설계한다.

## 2. 인증 로직과 서비스 로직간의 패키지 분리

- 서비스 코드와 인증 코드를 명확히 분리하여 관리하도록 한다.
    - 서비스 관련 코드는 app 패키지에 위치시키고, 인증 관련 코드는 security 패키지에 위치시킨다.
- 리팩터링 과정에서 패키지간의 양방향 참조가 발생한다면 단방향 참조로 리팩터링한다.
    - app 패키지는 security 패키지에 의존할 수 있지만, 반대로 security 패키지는 app 패키지에 의존하지 않도록 한다.
- 인증 관련 작업은 security 패키지에서 전담하도록 설계하여, 서비스 로직이 인증 세부 사항에 의존하지 않게 만든다.

## 힌트

### 1. 인터셉터 설정

WebMvcConfigurer 예시

```java

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AAAInterceptor());
        registry.addInterceptor(new BBBInterceptor()).addPathPatterns("/members");
    }
}
```

HandlerInterceptor

```java
public class BasicAuthenticationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // ...
```

### 2. 하나의 작업을 수행하는 Interceptor

- 누구인지 식별하는 목적의 인터셉터와 식별된 사용자인지 여부를 확인 인터셉터 처럼 같은 인증 프로세스이지만 목적에 맞는 인터셉터로 분리할 수 있다
- 예를 들어, /members 요청 시 베이직 인증 인터셉터에서 식별을 성공한 후 인증 객체가 있는지 인증 여부 확인을 할 경우 베이직 인증 인터셉터가 아닌 다른 인터셉터로 인증 정보를 전달해야한다.
- 인터셉터 간 인증 성공 정보를 전달하는 방법은 Session에 유지할 수 있고 Request 객체에 담아서 유지할 수 있다. 그 외 방법도 존재한다.

### 3. 패키지간의 양방향 참조

- 인증 관련 로직을 단순히 app 패키지에서 security 패키지로 옮긴다면 양방향 참조가 발생한다.
    - app에서는 security를 당연히 의존하는 상황에서
    - BasicAuthenticationInterceptor에서 MemberRepository와 Member를 의존하고 있으니
    - 패키지간 양방향 참조가 발생중이다.
- 양방향 의존에서 단방향 의존 만들기위해서 중간 객체를 이용하여 의존성 사이클을 제거할 수 있다.
    - [우아한 객체지향 세미나 - 패키지 의존 문제 해결](https://youtu.be/dJ5C4qRqAgA?t=2941)
      ![image](https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/5e1eb712c530482a9737ac83016f30bd)

# 3단계

## 1. SecurityFilterChain 적용

스프링 시큐리티는 기본적으로 필터를 활용한다. 기존에 구현한 인터셉터 기반의 인증을 필터 방식으로 전환한다.

- Interceptor 를 filter 로 구현
- 필터를 동작시킬 환경을 구축한다
    - 보안 필터를 서블릿 필터 체인에 등록하지 않고 아래 그림 참고해서 구성
    - `DelegatingFilterProxy` -> `FilterChainProxy` -> `SecurityFilterChain`
- 추가로 필요한 코드가 있는데 스프링 시큐리티 코드를 참고하여 작성하는 것을 권장한다. 스프링 시큐리티 필터 체인 공식 문서를 활용하여 구현해도 좋다. 다만 너무 똑같이 만들려고 하면 오히려 힘들 수 있으니
  주의한다.

![image](https://docs.spring.io/spring-security/reference/_images/servlet/architecture/securityfilterchain.png)

## 2. AuthenticationManager를 활용한 인증 추상화

스프링 시큐리티는 다양한 인증 방식을 제공하고 있으며, 이 방식들을 `AuthenticationManager`라는 형태를 활용하여 추상화해 놓았다. 기존에 구현한 인증 처리 로직을 추상화 하고 인증 방식에 따른
구현체를
구현한다. 아래 이미지에서 Authentication Filter 이후 (3번) 인증 과정에 집중하여 리팩터링한다.

- `Authentication`, `AuthenticationManager`, `AuthenticationProvider` 인터페이스를 작성하여 구조의 뼈대를 만든다.
- `ProviderManager`, `DaoAuthenticationProvider`를 구현하며 흐름을 제어하는 코드를 만든다.
- 그 외 필요한 객체(`UsernamePasswordAuthenticationToken` 등)를 구현하며 기능을 완성한 후 기존 필터에서 인증 로직을 분리한다.

![image](https://nextstep-storage.s3.ap-northeast-2.amazonaws.com/ad3da2895e864a3baca9861dfdb99650)

- 주요 클래스: `AuthenticationManager`, `ProviderManager`, `AuthenticationProvider`, `DaoAuthenticationProvider`

## 3. SecurityContextHolder 적용

인증 성공 후 만든 Authentication(인증 정보 객체)을 `세션`으로 관리하고 있었는데, `스레드 로컬`에 보관하도록 변경한다. 각 필터에서 스레드 로컬에 보관된 인증 정보에 접근을 할 수 있도록 한다.

- `SecurityContext`, `SecurityContextHolder`를 작성하여 `Authentication` 객체를 보관할 구조의 뼈대를 만든다.
- `BasicAuthenticationFilter`에서 `SecurityContextHolder`를 활용하여 `Authentication` 객체 보관하도록 한다.

![image](https://docs.spring.io/spring-security/reference/_images/servlet/authentication/architecture/securitycontextholder.png)

- 주요 클래스: SecurityContextHolder, SecurityContext

## 4. SecurityContextHolderFilter 구현

`UsernamePasswordAuthenticationFilter`는 로그인 성공 후 세션에 인증 정보(`Authentication`)를 보관하여 다음 요청에서 인증 정보를 활용한다. 세션에 담긴 인증 정보를
`SecurityContextHolder`로 옮기는 `SecurityContextHolderFilter` 필터를 구현하고, 세션 정보를 관리하는 `SecurityContextRepository`를 구현한다.

- 인증 정보를 보관하는 `SecurityContextRepository`를 구현한다. 실제 코드에서는 `SecurityContextRepository`는 인터페이스고,
  `HttpSessionSecurityContextRepository`가 구현체이다. 스프링 시큐리티에서는 세션 외 다른 방법으로 인증 정보를 유지할 수 있다.
- `SecurityContextHolderFilter`를 구현하여 `SecurityContextRepository`에 있는 인증 정보를 `SecurityContextHolder`로 옮겨오고 해당 필터를 적절한
  순서에
  등록한다.
- 기능의 정상 동작 여부를 판단하기 위해 ✅ `login_after_members` 테스트를 활용한다.

```java

@DisplayName("일반 회원은 회원 목록 조회 불가능")
@Test
void user_login_after_members() throws Exception {
    MockHttpSession session = new MockHttpSession();

    ResultActions loginResponse = mockMvc.perform(post("/login")
            .param("username", TEST_USER_MEMBER.getEmail())
            .param("password", TEST_USER_MEMBER.getPassword())
            .session(session)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    );

    loginResponse.andExpect(status().isOk());

    ResultActions membersResponse = mockMvc.perform(get("/members")
            .session(session)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    );

    membersResponse.andExpect(status().isForbidden());
}    
```

## 5. 추가 리펙터링 (선택 요구사항)

- 그 외 스프링 시큐리티와 유사한 구조로 리팩터링이 가능한 부분을 개선한다.
  ex) `AbstractAuthenticationProcessingFilter` 추상화 등

## 힌트

### 1. DelegatingFilterProxy 설정 코드

- DelegatingFilterProxy와 FilterChainProxy를 활용하여 SecurityFilterChain을 구성하는 예시.
- 실제 시큐리티 코드를 가져올 필요도 없고 힌트의 샘플 코드를 그대로 따를 필요는 없다.

```java

@Bean
public DelegatingFilterProxy delegatingFilterProxy() {
    SecurityFilterChain securityFilterChain = new DefaultSecurityFilterChain(
            AnyRequestMatcher.INSTANCE,
            List.of(
                    new BasicAuthenticationFilter(userDetailsService),
                    new FormLoginAuthenticationFilter(userDetailsService),
                    new CheckAuthenticationFilter()
            )
    );

    List<SecurityFilterChain> filterChains = List.of(securityFilterChain);
    return new DelegatingFilterProxy(new FilterChainProxy(filterChains));
}
```

- 참고로 적절한 요청에 따라 필터 동작 여부를 판단하는 로직은 다음을 참고할 수 있다.

```java
public class CheckAuthenticationFilter extends GenericFilterBean {
    private static final MvcRequestMatcher DEFAULT_REQUEST_MATCHER = new MvcRequestMatcher(HttpMethod.GET,
            "/members");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!DEFAULT_REQUEST_MATCHER.matches((HttpServletRequest) request)) {
            chain.doFilter(request, response);
        }
        //...
```

- `SecurityFilterChain` 적용 시 아래 작업 순서를 참고한다.
    - 주요 클래스인 `DelegatingFilterProxy`, `FilterChainProxy`, `SecurityFilterChain`를 구현하며 Filter Chain의 뼈대를 구축한다.
    - 기존에 만들었던 Interceptor를 Filter로 전환한다. 이 때 Interceptor와 Filter의 다른 동작 방식을 인지하고 맞게 수정한다.

### 2. 인증 로직 추상화

#### 추상화 흐름

- Request의 정보(username과 password)를 활용하여 인증 객체를 만든다. 이 때 객체는 `Authentication`를 활용한다.
- 앞서 만든 인증 객체를 활용하여 `AuthenticationManager`의 authenticate메서드를 호출하고 응답 결과를 받는다. 리턴 객체는 마찬가지로 `Authentication`를 활용한다.
- `Authentication`를 활용해서 인증 결과를 저장한다.

#### 추상화 시각화

- 인증 방식에 따라 authenticate 메서드 내에서 처리 방식이 달라진다. 조건문으로 구현하기 보다는 **인증 처리 객체를 별도로 두는 것을 추천**한다. 아래의 이미지를 참고해서
  `ProviderManager`와 `AuthenticationProvider`의 개념을 활용할 수 있다.

![AuthenticationManager](https://docs.spring.io/spring-security/reference/_images/servlet/authentication/architecture/providermanager-parent.png)
![ProviderManager](https://docs.spring.io/spring-security/reference/_images/servlet/authentication/architecture/providermanager.png)

- 참고할 요소
    - AuthenticationManager
    - ProviderManager
    - AuthenticationProvider

### 3-1 . SecurityContextHolder 활용 예시

- SecurityContextHolder 구현 시 여러 전략(strategy)로 인해 구현의 복잡도가 올라갈 수 있는데, ThreadLocal에 저장하는 경우만 고려한다면 전략들을 없애고 아래와 같이 간단하게
  구현할 수 있다.

```java
public class SecurityContext implements Serializable {
    private Authentication authentication;

    public SecurityContext() {
    }

    public SecurityContext(Authentication authentication) {
        this.authentication = authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public Authentication getAuthentication() {
        return authentication;
    }
}
```

```java
public class SecurityContextHolder {
    private static final ThreadLocal<SecurityContext> contextHolder;

    static {
        contextHolder = new ThreadLocal<>();
    }

    public static void clearContext() {
        contextHolder.remove();
    }

    public static SecurityContext getContext() {
        SecurityContext ctx = contextHolder.get();

        if (ctx == null) {
            ctx = createEmptyContext();
            contextHolder.set(ctx);
        }

        return ctx;
    }

    public static void setContext(SecurityContext context) {
        if (context != null) {
            contextHolder.set(context);
        }
    }

    public static SecurityContext createEmptyContext() {
        return new SecurityContext();
    }
}
```

### 3-2. SecurityContextHolder 적용 작업 순서

- 주요 클래스인 SecurityContextHolder, SecurityContext를 구현하며 Thread Local을 활용한 보관 공간을 먼저 구축한다.
- Session을 활용하여 인증 객체(Authentication)를 보관하던 부분을 SecurityContextHolder을 이용하여 보관하도록 수정한다.

#### As-is

```java
HttpSession session = httpRequest.getSession();
session.setAttribute("SPRING_SECURITY_CONTEXT",authResult);
```

#### To-be

```java
SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
securityContext.setAuthentication(authResult);
SecurityContextHolder.setContext(securityContext);
```

### 3-3 - Form Login이 제대로 동작하지 않을 경우

- 인증 객체 유지 방법을 세션에서 스레드 로컬로 변경할 경우 Form Login이 제대로 동작하지 않을 수 있다. 이유는 인증 객체가 세션으로 보관되지 않아서 로그인 후 두번째 요청 시 인증 객체를 받아오기 어려워지기 때문이다.
- 인증 객체를 보관하기 위해 `SecurityContextHolderFilter`와 SecurityContextRepository를 구현한다.
- Form 로그인 인증인 경우를 위해 `HttpSessionSecurityContextRepository`를 참고해서 구현한다. 심화 학습을 희망할 경우 `SecurityContextRepository`의을 추상화 하여 다른 종류의 `SecurityContextRepository` 도 구현해본다.
- 참고 문서: [SecurityContextRepository](https://docs.spring.io/spring-security/reference/servlet/authentication/persistence.html#securitycontextrepository)

#### 테스트가 깨지는 경우!
- LoginTest 의 login_success 내용중 일부를 아래와 같이 테스트를 수정한다.
```java
// AS-IS
HttpSession session = loginResponse.andReturn().getRequest().getSession();
        assertThat(session).isNotNull();
        assertThat(session.getAttribute("SPRING_SECURITY_CONTEXT")).isNotNull();

// TO-BE
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
assertThat(authentication).isNotNull();
```
















