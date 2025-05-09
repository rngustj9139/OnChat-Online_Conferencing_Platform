package koo.online_education_platform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController { // SecurityConfig 확인 (Spring Security를 활용한 로그인 구현 완료)

    @GetMapping("/chatlogin")
    public String goLogin(){
        return "/chatlogin";
    }

}
