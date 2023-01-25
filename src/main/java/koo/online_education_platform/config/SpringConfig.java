package koo.online_education_platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * - 여기서 엔드포인트란 일종의 "통신의 도작지점" 이라고 생각하면 될 것 같다. 즉 특정한 통신이 어떤 엔드포인트에 도착했을 때 어떤 행위를 하게 만들것이다라는 것이다.
 * - 아래에서 처럼 Endpoint 를 "/ws-stomp" 로 설정해두면 웹소켓 통신이 /ws-stomp 로 도착할때 우리는 해당 통신이 웹 소켓 통신 중에서 stomp 통신인 것을 확인하고, 이를 연결한다는 의미이다.
 * - 추가로 /sub 로 도착하는 것은 메시지를 구독(sub) 할 때 사용하고, "/pub" 로 도착하는 것은 메시지를 송신 할 때 사용하는 엔드포인트가 되는 것이다.
 */
@Configuration
@EnableWebSocketMessageBroker
public class SpringConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // stomp 접속 주소 url => /ws-stomp
        registry.addEndpoint("/ws-stomp") // 연결될 엔드포인트
                .withSockJS(); // SocketJS 를 연결한다는 설정
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지를 구독하는 요청 url => 즉 메시지 받을 때
        registry.enableSimpleBroker("/sub");

        // 메시지를 발행하는 요청 url => 즉 메시지 보낼 때
        registry.setApplicationDestinationPrefixes("/pub");
    }

}
