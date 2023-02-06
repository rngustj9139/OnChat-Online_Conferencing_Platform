package koo.online_education_platform.service.social;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 일반 세션 로그인
// 시큐리티 설정에서 loginProcess 에 해당하는 요청이 들어왔을 때 아래의 내용이 실행됨
// 더 자세히는 loginProcess 해당하는 "/login" 요청이 들어오면
// UserDetailsService의 타입으로 IoC(Bean으로 등록된 클래스) 되어있는 loadUserByUsername 메서드가 실행됨 (후처리 가능)
// 로그인 시 스프링 시큐리티 세션 안에 Authentication이 들어가고 Authentication 안에는 UserDetails or OAuth2User가 구현 된 객체가 들어가야하는데 그러한 객체를 Authentication안에 넣어주는 역할을 한다.
@Service
public class PrincipalDetailService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

}
