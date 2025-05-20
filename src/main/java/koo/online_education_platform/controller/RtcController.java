package koo.online_education_platform.controller;

import koo.online_education_platform.dto.WebSocketMessage;
import koo.online_education_platform.service.chatService.RtcChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RtcController {

    private final RtcChatService rtcChatService;

    @PostMapping("/webrtc/usercount") // ICE를 위한 chatList 인원 확인 (RTC 채팅방에 인원이 없다면 ICE를 수행하지 않아도 된다) (resources/static/js/rtc/webrtc_client.js 확인)
    public String webRTC(@ModelAttribute WebSocketMessage webSocketMessage) { // HTTP 요청 파라미터가 WebSocketMessage (커스텀) 객체와 같은 파라미터를 갖는다면 자동으로 HTTP 요청 파라미터를 객체에 매핑
        log.info("MESSAGE : {}", webSocketMessage.toString());

        return Boolean.toString(rtcChatService.findUserCount(webSocketMessage));
    }

}