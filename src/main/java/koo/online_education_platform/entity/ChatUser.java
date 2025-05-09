package koo.online_education_platform.entity;

import koo.online_education_platform.dto.ChatUserDto;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickName; // 소셜에서 제공받은 유저명: 유저 닉네임
    private String passwd;
    private String email; // 소셜에서 제공받은 이메일
    private String provider; // 소셜 제공자: e.g. 네이버, 카카오 etc

    public static ChatUser of (ChatUserDto chatUserDto) { // ChatUser chatUser = ChatUser.of(chatUserDto);
        ChatUser chatUser = ChatUser.builder()
                .nickName(chatUserDto.getNickName())
                .passwd(chatUserDto.getPasswd())
                .email(chatUserDto.getEmail())
                .provider(chatUserDto.getProvider())
                .build();

        return chatUser;
    }

}
