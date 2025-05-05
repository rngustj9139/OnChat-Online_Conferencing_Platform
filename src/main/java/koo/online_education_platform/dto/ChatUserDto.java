package koo.online_education_platform.dto;

import koo.online_education_platform.entity.ChatUser;
import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserDto { // 로그인시 사용되는 DTO

    private Long id; // DB 저장되는 id: PK
    private String nickName; // 소셜에서 제공받은 유저명: 유저 닉네임
    private String email; // 소셜에서 제공받은 이메일
    private String provider; // 소셜 제공자: e.g. 네이버, 카카오 etc

    public static ChatUserDto of (ChatUser chatUserEntity) { // ChatUserDto dto = ChatUserDto.of(chatUser);
        ChatUserDto chatUserDto = ChatUserDto.builder()
                .id(chatUserEntity.getId())
                .nickName(chatUserEntity.getNickName())
                .email(chatUserEntity.getEmail())
                .provider(chatUserEntity.getProvider())
                .build();

        return chatUserDto;
    }

}
