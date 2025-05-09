package koo.online_education_platform.config;

import koo.online_education_platform.entity.ChatUser;
import koo.online_education_platform.repository.ChatUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class TestUserDataLoader implements ApplicationRunner {

    private final ChatUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run (ApplicationArguments args) throws Exception {
        // 이미 생성되어 있지 않다면
        if (userRepository.findByNickName("testuser").isEmpty()) {
            ChatUser test = ChatUser.builder()
                    .nickName("testuser")
                    .passwd(passwordEncoder.encode("testpass"))
                    .email("test@example.com")
                    .provider("DEFAULT")
                    .build();
            userRepository.save(test);
            System.out.println(">>> 테스트용 유저(testuser) 생성 완료");
        }
    }


}
