package koo.online_education_platform.dto;

import com.sun.istack.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

/**
 * STOMP를 통해 pub/sub 모델을 사용하면 자동 구독자 관리가 가능함
 * 따라서 따로 세션 관리를 수행하는 코드를 작성할 필도 없고, 메시지를 다른 세션의 클라이언트에게 발송하는 코드를 구현할 필요가 없다.
 * @Data: @Getter, @Setter, @ToString
 */
@Data
@Builder
public class ChatRoomDto {

    /*
    @NotNull: null이 아니어야 함
    @NotEmpty: null도 아니고, 비어있지도 않아야 함 ("")
    @NotBlank: null도 아니고, 공백 문자만으로 구성되지 않아야 함 (" ")
     */
    @NotNull
    private String roomId; // 채팅방 아이디

    private String roomName; // 채팅방 이름
    private int userCount; // 채팅방 인원수
    private int maxUserCnt; // 채팅방 최대 인원 제한
    private String roomPwd; // 채팅방 삭제시 필요한 pwd
    private boolean secretChk; // 채팅방 잠금 여부

    public enum ChatType {
        MSG, RTC
    }

    private ChatType chatType;

    /**
     * 텍스트 채팅의 경우 <String, String> => <userUUID, userName>
     * 화상 채팅의 경우 <String, WebSocketSession> => <userUUID, WebSocketSession>
     * ?: 제네릭 와일드카드
     * Map: 인터페이스, HashMap: 구현체 (내부적으로 해시 테이블 기반으로 동작, 빠른 검색과 삽입이 특징)
     */
    private HashMap<String, ?> userList;

}
