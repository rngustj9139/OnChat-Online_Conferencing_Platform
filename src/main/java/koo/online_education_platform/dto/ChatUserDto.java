package koo.online_education_platform.dto;

import koo.online_education_platform.entity.ChatUser;
import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserDto {

    private String nickName; // 소셜에서 제공받은 유저명: 유저 닉네임
    private String passwd;
    private String email; // 소셜에서 제공받은 이메일
    private String provider; // 소셜 제공자: e.g. 네이버, 카카오 etc

    public static ChatUserDto of (ChatUser chatUser) { // ChatUserDto chatUserDto = ChatUserDto.of(chatUser);
        ChatUserDto chatUserDto = ChatUserDto.builder()
                .nickName(chatUser.getNickName())
                .passwd(chatUser.getPasswd())
                .email(chatUser.getEmail())
                .provider(chatUser.getProvider())
                .build();

        return chatUserDto;
    }

}
