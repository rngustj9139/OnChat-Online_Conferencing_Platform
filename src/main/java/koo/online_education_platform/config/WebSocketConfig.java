package koo.online_education_platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * - 여기서 엔드포인트란 일종의 "통신의 도작 지점" 이라고 생각하면 된다. 즉 특정한 통신이 어떤 엔드포인트에 도착 했을 때 특정한 행위를 수행 하게 할 것을 의미한다.
 * - 아래에서 처럼 Endpoint를 "/ws-stomp"로 설정해두면 웹소켓 통신이 /ws-stomp로 도착할때 우리는 해당 통신이 웹 소켓 통신 중에서 stomp 통신인 것을 확인하고, 이를 연결한다는 의미 (핸드쉐이크 수행)이다.
 * - 추가로 /sub 로 도착하는 것은 메시지를 구독(sub) 할 때 사용하고, "/pub" 로 도착하는 것은 메시지를 송신 할 때 사용하는 엔드포인트가 된다.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints (StompEndpointRegistry registry) {
        // stomp 접속 주소 url: /ws-stomp
        registry.addEndpoint("/ws-stomp") // 연결될 엔드포인트 (연결을 위해 처음에 핸드쉐이크를 수행할 주소)
                .withSockJS(); // SocketJS 를 연결한다는 설정
    }

    @Override
    public void configureMessageBroker (MessageBrokerRegistry registry) { // STOMP의 Message Broker 설정
        // Topic을 구독하는 요청 url: 메시지 받을 때
        registry.enableSimpleBroker("/sub"); // 스프링에서 제공하는 내장 메세지 브로커 이용 (SimpleMessageBroker)

        // Topic에 메시지를 발행하는 요청 url: 메시지 보낼 때
        registry.setApplicationDestinationPrefixes("/pub"); // Message Handler로 라우팅 된다 (바로 메세지 브로커에게 메세지가 가는 것이 아닌 메세지의 처리나 가공이 필요할때 핸들러를 거친다.) (여기서 이후 이용하게 될 핸들러는 ChatController의 enterUser이다)
    }

}
