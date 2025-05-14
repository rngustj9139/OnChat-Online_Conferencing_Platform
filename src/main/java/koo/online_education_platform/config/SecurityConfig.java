package koo.online_education_platform.config;

import koo.online_education_platform.service.social.PrincipalDetailService;
import koo.online_education_platform.service.social.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PrincipalDetailService detailService;
    private final PrincipalOauth2UserService oauth2UserService;

    @Override
    protected void configure (HttpSecurity http) throws Exception { // 기본 설정 및 소셜 로그인 (public 접근 제어자: 같은 클래스, 자식 클래스, 외부 클래스에서 호출 가능, private 접근 제어자: 같은 클래스 내에서만 호출 가능, protected 접근 제어자: 같은 클래스, 자식 클래스만 호출 가능)
        http.csrf().disable() // Spring Security의 기본 csrf 보호 기능을 disable (CSRF e.g. 사용자가 A 사이트에 로그인 (세션 쿠키 존재) → 악성 사이트 B에 방문 → B 사이트가 사용자의 세션 쿠키를 이용해 A 사이트에 요청을 보냄 (예: 게시글 작성, 결제 등) → 사용자는 의도하지 않은 행동을 하게 됨)
                   .authorizeHttpRequests()
                   // "/" 아래로 접근하는 모든 유저에 대해서 허용 => 즉 모든 경로에 대해서 허용
                   // 로그인 안해도 채팅은 가능하기 때문에 로그인 없이도 모든 경로에 접근할 수 있도록 설정
                   .antMatchers("/**").permitAll()
                .and()
                   // Security의 기본 login 페이지가 아닌 커스텀 로그인 페이지를 사용하기 위한 설정
                   .formLogin().loginPage("/chatlogin").permitAll()
                   .usernameParameter("nickName")
                   .passwordParameter("passwd")
                   .loginProcessingUrl("/login") // 로그인 요청 url
                   .defaultSuccessUrl("/") // 로그인 완료 시 요청 url
                .and()
                   .logout().logoutUrl("/logout").permitAll() // 로그인 아웃 시 url
                   .logoutSuccessUrl("/") // 성공적으로 로그아웃 햇을 때 url
                .and()
                   .oauth2Login() // 소셜 로그인 사용 여부
                   .loginPage("/chatlogin") // 소셜 로그인 진행 시 사용할 url
                   .userInfoEndpoint()
                   // SNS 로그인이 완료된 뒤 후처리가 필요함. 엑세스토큰 + 사용자 프로필 정보
                   .userService(oauth2UserService);
    }

    @Override
    protected void configure (AuthenticationManagerBuilder auth) throws Exception { // 일반 (디폴트) 로그인
        auth.userDetailsService(detailService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

}
