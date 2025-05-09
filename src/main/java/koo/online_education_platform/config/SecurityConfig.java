package koo.online_education_platform.config;

import koo.online_education_platform.service.social.PrincipalDetailService;
import koo.online_education_platform.service.social.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

    // Security 를 이용한 각종 권한 접근 경로 등 설정
    @Override
    protected void configure (HttpSecurity http) throws Exception {
        http.csrf().disable()
                   .authorizeHttpRequests()
                   // "/" 아래로 접근하는 모든 유저에 대해서 허용 => 즉 모든 경로에 대해서 허용
                   // 로그인 안해도 채팅은 가능하기 때문에 로그인 없이도 모든 경로에 접근할 수 있도록 설정
                   .antMatchers("/**").permitAll()
                .and()
                   // Security 의 기본 login 페이지가 아닌 커스텀 페이지를 사용하기 위한 설정
                   // 로그인 페이지 url
                   .formLogin().loginPage("/chatlogin").permitAll()
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

    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

}
