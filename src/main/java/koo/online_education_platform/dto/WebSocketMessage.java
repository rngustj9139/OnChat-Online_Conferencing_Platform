package koo.online_education_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * - WebRTC 연결 시 사용되는 클래스로 WebSocket 연결 정보를 주고 받을 때 사용된다.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketMessage {

    private String from; // 보내는 유저 UUID
    private String type; // 메시지 타입
    private String data; // roomId
    private Object candidate; // 상태
    private Object sdp; // sdp 정보

}
