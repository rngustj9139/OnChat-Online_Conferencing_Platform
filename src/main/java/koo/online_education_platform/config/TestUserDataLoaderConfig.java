package koo.online_education_platform.config;

import koo.online_education_platform.entity.ChatUser;
import koo.online_education_platform.repository.ChatUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class TestUserDataLoaderConfig implements ApplicationRunner {

    private final ChatUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run (ApplicationArguments args) throws Exception {
        // 이미 생성되어 있지 않다면
        if (userRepository.findByNickName("admin").isEmpty()) { // isEmpty() 내장함수는 Optional이 값을 가지고 있지 않을 때 true를 반환
            ChatUser test = ChatUser.builder()
                    .nickName("admin")
                    .passwd(passwordEncoder.encode("admin"))
                    .email("admin@example.com")
                    .provider("DEFAULT")
                    .build();
            userRepository.save(test);
            log.info(">>> 테스트용 유저(admin) 생성 완료");
        }
    }

}
