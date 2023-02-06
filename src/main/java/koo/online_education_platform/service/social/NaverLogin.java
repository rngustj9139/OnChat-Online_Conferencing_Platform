package koo.online_education_platform.service.social;

import java.util.Map;
import java.util.UUID;

public class NaverLogin implements SocialLogin {

    private Map<String, Object> naverAttributes;

    public NaverLogin(Map<String, Object> naverAttributes) {
        this.naverAttributes = naverAttributes;
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        Map<String, Object> map = (Map<String, Object>) naverAttributes.get("response");

        return (String) map.get("email");
    }

    @Override
    public String getNickName() {
//        Map<String, Object> map = (Map<String, Object>) naverAttributes.get("response");
//
//        return (String) map.get("nickname");
        String nickName = "naverUser_" + UUID.randomUUID().toString();

        return nickName;
    }

}
