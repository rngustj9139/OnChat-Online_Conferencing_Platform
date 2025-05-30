package koo.online_education_platform.dto;

import lombok.*;

/**
 * - 채팅 내용을 위한 DTO
 */
@Getter @Setter
@Builder
/*
  (빌더 패턴 e.g.)
  @Builder
  public class Member {
      private String name;
      private int age;
      private String email;
  }

  Member member = Member.builder()
                        .name("John")
                        .age(25)
                        .email("john@example.com")
                        .build();
*/
@AllArgsConstructor
@NoArgsConstructor
public class ChatDto {

    // 메시지  타입 : 입장, 채팅
    // 메시지 타입에 따라서 동작하는 구조가 달라진다.
    // 입장과 퇴장 ENTER 과 LEAVE 의 경우 입장/퇴장 이벤트 처리가 실행되고,
    // TALK 는 말 그대로 내용이 해당 채팅방을 SUB 하고 있는 모든 클라이언트에게 전달된다.
    public enum MessageType {
        ENTER, TALK, LEAVE;
    }

    private MessageType type; // 메시지 타입
    private String roomId; // 방 번호
    private String sender; // 채팅을 보낸 사람
    private String message; // 메시지
    private String time; // 채팅 발송 시간

    /* 파일 업로드 관련 변수 */
    private String s3DataUrl; // 파일 업로드 url
    private String fileName; // 파일이름
    private String fileDir; // s3 파일 경로

}
