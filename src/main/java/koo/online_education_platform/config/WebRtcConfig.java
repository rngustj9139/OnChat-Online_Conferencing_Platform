package koo.online_education_platform.config;

import koo.online_education_platform.rtc.SignalHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * - WebRTC를 위한 Signaling 서버는 WebSocket을 사용하기 때문에 이에 대한 설정이 필요
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebRtcConfig implements WebSocketConfigurer {

    private final SignalHandler signalHandler;

    // Signaling 요청이 왔을 때 (/signal url로 요청이 왔을 때) 아래의 WebSockerHandler가 동작하도록 registry에 설정
    // 요청은 클라이언트 접속, close, 메시지 발송 등에 대한 특정 메서드를 호출이 있다.
    @Override
    public void registerWebSocketHandlers (WebSocketHandlerRegistry registry) {
        registry.addHandler(signalHandler, "/signal")
                .setAllowedOrigins("*");
    }

    // WebSocket 세션의 메시지 버퍼 크기를 설정
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer () {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192); // 텍스트 메시지의 최대 버퍼 크기를 8192 bytes(8KB)로 지정
        container.setMaxBinaryMessageBufferSize(8192); // 바이너리 메시지 (예: 파일, 이미지 등)의 최대 크기를 8KB로 지정

        return container;
    }

}
