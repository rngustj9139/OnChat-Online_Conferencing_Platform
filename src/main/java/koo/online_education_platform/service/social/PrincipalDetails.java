package koo.online_education_platform.service.social;

import koo.online_education_platform.dto.ChatUserDto;
import koo.online_education_platform.entity.ChatUser;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Data
public class PrincipalDetails implements UserDetails, OAuth2User { // UserDetails는 Spring Security에서 인증된 사용자의 정보를 담는 기본 인터페이스, OAuth2User는 구글, 네이버, 카카오 같은 OAuth2 인증 제공자로부터 받은 사용자 정보를 표현하는 인터페이스

    // ChatUserDTO
    private ChatUserDto user;

    // 소셜 로그인 유저의 정보 확인을 위한 attributes
    private Map<String, Object> attributes;

    // 소셜 유저 타입 정보 -> 네이버, 카카오, 일반 등
    private String provider;

    // 일반 유저
    public PrincipalDetails (ChatUser user) {
        this.user = ChatUserDto.of(user);
        this.provider = user.getProvider();
    }

    // OAuth2User 유저 -> 소셜 로그인 유저
    public PrincipalDetails (ChatUserDto user, Map<String, Object> attributes, String provider){
        this.user = user;
        this.attributes = attributes;
        this.provider = provider;
    }

    @Override
    public Map<String, Object> getAttributes () {
        return null;
    }

    @Override
    public String getName () {
        return user.getNickName();
    }

    /*
     해당 유저의 권한을 return
     원래는 회원 가입 시 유저의 권한을 설정해두고 해당 유저의 권한을 return 해야하나
     현재는 DB 를 사용해서 로그인을 하는게 아니라 소셜 로그인을 하는 것이기 때문에
     모든 유저의 권한은 "user" 로 return 한다.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities () {
        Collection<GrantedAuthority> role = new ArrayList<>();

        role.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return "user";
            }
        });

        return role;
    }

    @Override
    public String getPassword () {
        return user.getPasswd(); // 추가해야 로그인시 raw 비밀번호와 암호화 (encoding) 된 비밀번호가 일치
    }

    @Override
    public String getUsername () {
        return user.getNickName();
    }

    @Override
    public boolean isAccountNonExpired () {
        return true;
    }

    @Override
    public boolean isAccountNonLocked () {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired () {
        return true;
    }

    @Override
    public boolean isEnabled () {
        return true;
    }

}
