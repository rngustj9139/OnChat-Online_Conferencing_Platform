package koo.online_education_platform.service.social;

import koo.online_education_platform.dto.ChatUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

// OAuth2 로그인 시 -> 즉 소셜 로그인 시 DefaultOAuth2UserService 아래의 loadUser 메서드가 실행됨
// 즉 OAuth2 로그인 후 후처리 - 회원 가입, 회원 정보에 따른 등록 등을 수행
// 소셜 로그인 후 스프링 시큐리티 세션에 로그인 정보를 담는 기능을 담당
/**
 * 다음과 같은 순서로 동작
 * 네이버 로그인 버튼 클릭 -> 네이버 로그인창 -> 로그인 완료 -> code 리턴(OAuth-client 라이브러리) -> AccessToken 요청 -> userRequest 정보 -> loadUser 함수 호출 -> 네이버 회원 프로필 받아오기
 */
@Service
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 매개변수로 넘어온 userRequest 를 loaduser 를 실행해서 유저 정보가 담긴 oauth2user로 변환
        OAuth2User oauth2user = super.loadUser(userRequest);

        log.info("clientRegistration 정보 [{}] ", userRequest.getClientRegistration());
        log.info("accessToken 정보 [{}] ", userRequest.getAccessToken().getTokenValue());

        return oAuth2UserLogin(userRequest, oauth2user);
    }

    private OAuth2User oAuth2UserLogin(OAuth2UserRequest userRequest, OAuth2User oAuth2User){
        // Attribute 를 파싱해서 공통 객체로 묶기!! => 소셜 로그인 마다 다른 정보가 들어옴으로 쉽게 관리하기 위해서
        SocialLogin login = null;

        // provider 정보 확인 => 어떤 SNS 로 로그인했는지 확인
        String provider = userRequest.getClientRegistration().getRegistrationId();

        if ("kakao".equals(provider)) {
            // 카카오 로그인인 경우 KaKaoLogin 클래스에 소셜 로그인 정보가 담긴
            // oAuth2User.getAttributes() 를 보내주고 정보를 담는다
            login = new KaKaoLogin(oAuth2User.getAttributes());
        } else if ("naver".equals(provider)) {
            // 네이버 로그인인 경우 NaverLogin 클래스를 소셜 로그인 정보가 담긴
            // oAuth2User.getAttributes() 를 보내주고 정보를 담는다
            login = new NaverLogin(oAuth2User.getAttributes());
        }

        // ChatUser 에 소셜 로그인 후 받아서 나눠진 정보를 담는다
        ChatUserDto user = ChatUserDto.builder()
                .nickName(login.getNickName())
                .email(login.getEmail())
                .provider(login.getProvider())
                .build();

        log.info("ChatUserDto = {}", user);

        // 이 정보들은 SecuritySession 의 Authentication 안에 담김
        return new PrincipalDetails(user, oAuth2User.getAttributes(), user.getProvider());
    }

}
