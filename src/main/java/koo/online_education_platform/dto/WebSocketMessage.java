package koo.online_education_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebRTC 연결 시 사용되는 클래스로 초기 연결 정보를 교환할 때 사용된다.
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketMessage {

    private String from; // 보내는 유저 UUID
    private String type; // 메시지 타입
    private String data; // roomId
    private Object candidate; // 상태
    /*
     * WebRTC에서는 두 브라우저(혹은 클라이언트)가 영상, 오디오, 데이터 채널로 통신하려면 다음과 같은 정보를 서로 알아야 하고 아래의 모든 정보를 담는 것이 SDP이다.
     * 내가 사용할 코덱 종류 (예: VP8, H264, Opus 등)
     * 사용할 IP 주소와 포트
     * 암호화 방식
     * 오디오/비디오 여부
     * 해상도/프레임레이트 제한 등
     */
    private Object sdp; // SDP 정보

}
