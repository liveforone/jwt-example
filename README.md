# Jwt 회원 관리 예제
> 버전이 업그레이드 될때마다 지속적으로 릴리즈 합니다.
> 쉽게 따라할 수 있는 jwt 사용 예제입니다.

# 1. 기술 스택
* Spring Boot 3.0.0  //필수 선언
* Language : Java17  //필수 선언
* DB : MySql  //어떤 db던 필수 선언
* ORM : Spring Data Jpa  //필수 선언
* Spring Security  //필수 선언
* LomBok  //선택
* Gradle  //선택
* Apache commons lang3  //선택(회원 닉네임 생성에 사용)
* jjwt-api:0.11.5  //필수 선언
* jjwt-impl:0.11.5  //필수 선언
* jjwt-jackson:0.11.5  //필수 선언
* jjwt들의 버전정보는 반드시 기입한다.

# 2. yml 설정
* 기존 설정값들은 그대로 쓰고 아래의 코드를 추가한다.
```
jwt:
  secret: VlwEyVBsYt9V7zq57TejMnVUyzblYcfPQye08f7MGVA9XkHa
```

# 3. 클래스 설명
## CommonUtils
* 해당 클래스는 객체의 null 체크나 
* 리다이렉트시 매번 선언되는 HttpHeader를 리턴하는 함수들로 구성되어있다.
* 이는 jwt와 연관은 없고 매번 필자가 프로젝트 시에 복사하여 사용하는 편의 기능 제공 클래스이다.
## Member
* 해당 클래스는 멤버 엔티티 클래스이다.
* UserDetails를 implements하고 있기때문에
* getUsername(), getPassword(), getAuthorities(), isAccountNonExpired()
* isAccountNonLocked(), isCredentialsNonExpired(), isEnabled()를 오버라이딩 하고 있다.
* implements했기 때문에 반드시 오버라이딩 해주어야한다.
* 다른것보다 특히 중요한것은 getAuthorities()이다.
* 권한을 넘겨주는 아주아주 중요한 메소드인데, 엔티티의 필드인 Role auth를 
* getValue()로 들고온 후에 SimpleGrantedAuthority 화 시켜서 GrantedAuthority 컬렉션에 넣어
* 리턴해주면 된다. 권한을 넣는 방법은 기호에 따르나 일반적으로 enum으로 권한을 만들어서 저장하는것이
* 보통의 경우이므로 해당 코드를 참조하면된다.
## Role
* 해당 enum은 ADMIN, MEMBER 두가지 값을 가지고 있고 db에 저장해 넣어보면 알겠지만
* ADMIN, MEMBER의 형태로 저장된다.
* 다만 값을 getValue()로 가져올때에는 ROLE_MEMBER, ROLE_ADMIN의 형태로 가져와지는데
* 이는 주의할 필요가 있다.
* 그 이유는 차차 설명한다.
## TokenInfo
* 토큰을 잠시 가지고있는(dto같은) 클래스이다.
* grantType는 토큰의 타입으로 여기서는 Bearer를 사용한다.
## JwtTokenProvider
* 토큰을 생성하고 검증하는 클래스이다.
* getAuthentication()는 토큰을 복호화 하여 토큰에 있는 정보를 꺼낸다.
* generateToken()는 TokenInfo에서 보았던 accessToken과 refreshToken을 생성한다.
* 토큰의 유효기간은 30분으로 설정했다.
* 1일 기준 : 24 * 60 * 60 * 1000 = 86400000
## JwtAuthenticationFilter
* 토큰을 추출하는 필터 클래스이다.
* 토큰을 header를 통해 넘어온다.
* 해당 header에서 토큰을 추출하고 검증하는 것이 doFilter()이다.
## CustomUserDetailService
* 서비스 로직은 두가지가 있다.
* 하나는 지금 설명하려는 CustomUserDetailService, 둘째는 MemberService이다.
* 해당 클래스는 UserDetailsService를 implements 한다.
* 따라서 loadUserByUsername()을 오버라이딩 하여야한다.
* createUserDetails()에서는 멤버의 권한을 가져와 비교한다.
* 어드민이면 roles를 ADMIN으로 멤버이면 roles를 MEMBER로 변경한다.
* 여기서 그냥 member.getAuth.getValue()를 하면안되나? 하는 생각이 들 수 있다
* 안된다. 왜냐하면 해당 값은 ROLE_ADMIN 또는 ROLE_MEMBER이다. 
* 즉 앞에 ROLE_ 이 붙는다. 이 값은 UserDetails에서 자동으로 붙여주는 값이다.
* 따라서 이렇게 값을 가져오면 런타임 에러가 뜬다. 조심하자 !!
## MemberService
* 이 클래스에서는 두가지만 살펴보면 된다. joinUser()와 login()메소드만 보면된다.
* 회원가입시에는 입력받은 비밀번호를 passwordEncoder()를 사용해서 암호화 시켜준다.
* 입력받은 이메일이 어드민 이메일이라면(미리 정해두었음) 권한은 ADMIN으로 
* 일반 멤버라면 권한은 MEMBER로 설정하여 db에 save해준다.
* 로그인 시에는 UsernamePasswordAuthenticationToken을 만들어주고
* authenticationManagerBuilder를 사용해서 Authentication를 생성하고,
* jwtTokenProvider에 Authentication을 넣어서 토큰을 생성한다.
* 그리고 그 토큰을 넘겨준다.
* 즉 로그인이 성공하면 토큰이 클라이언트로 토큰이 전동된다.
## SecurityConfig
* 해당 클래스는 시큐리티의 설정을 하는 클래스이다.
* 원래 시큐리티를 썻었다면 별로다를 것이 없다.
* passwordEncoder를 빈으로 등록하고
* filterChain을 작성하는데 세션부분이 중요하다.
* 토큰을 생성할것이기 때문에 세션정책은 STATELESS로 설정하여 세션이 작동하지 않도록 한다.
* authorizeHttpRequests로 url에 권한을 달아주고
* addFilterBefore을 사용해서 커스텀 필터를 달아준다. 현재 jwt를 사용하기로 하였기에
* JwtAuthenticationFilter를 넣어준다.

# 4. postman 사용법
* postman으로 api 사용할때에는 발급된 accessToken을 
* headers에 key로 Authorization을 주고 value에
* Bearer[토큰] 으로 넣어주면 된다.

# 5. jwt 설명
* jwt는 JSON을 Base64를 통해 인코딩해 직렬화한 것이다.
* 토큰 내부에는 위변조 방지를 위해 개인키를 통한 전자서명도 들어있다.
* .을 구분자로 하여 세가지 문자열이 조합된 구조를 띄고 있는데
* 헤더.내용.서명 의 순서이다.

# 6. jwt 토큰 발급 순서 
* 1.로그인
* 2.accessToken, refreshToken 발급
* 3.accessToken으로 요청
* 4.accessToken에 문제 없을시 응답
* 5.refreshToken으로 accessToken재발급

# 7. 정리
* 세션 인증은 서버가 세션정보를 가지고 있어야 하고 이를 조회하는 process가 필요하다.
* 이러한 상태를 stateful 이라고 한다.
* 하지만 토큰은 세션과는 달리 서버가 아닌 클라이언트에 저장된다.
* 따라서 서버의 부담을 줄일 수 있게된다.
* 토큰안에 데이터가 들어있기 때문에 클라이언트 단에서 위조를 판별하면 끝나기 때문이다.
* 특히 웹이 아닌 앱에서는 세션사용이 불가능하기에 토큰을 사용한다.
* 이러한 상태를 stateless 라고 한다.