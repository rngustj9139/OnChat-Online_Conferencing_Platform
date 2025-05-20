package koo.online_education_platform.rtc;

import com.fasterxml.jackson.databind.ObjectMapper;
import koo.online_education_platform.dto.ChatRoomDto;
import koo.online_education_platform.dto.ChatRoomMap;
import koo.online_education_platform.dto.WebSocketMessage;
import koo.online_education_platform.service.chatService.ChatServiceMain;
import koo.online_education_platform.service.chatService.RtcChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * Signaling Server (WebRTC에서 두 사용자 간의 실시간 통신을 설정하기 위해 필요한 초기 연결 정보를 교환하는 역할을 하는 서버 (누구와 통신하는지 파악하는 것을 돕는 서버, 서로의 IP주소, PORT 주소등을 교환) 역할을 담당하는 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SignalHandler extends TextWebSocketHandler {

    private final RtcChatService rtcChatService;
    private final ChatServiceMain chatServiceMain;
    private final ObjectMapper objectMapper = new ObjectMapper(); //  Java 객체와 JSON 간의 변환 (직렬화, 역직렬화)을 담당

    // roomID to room Mapping
    private Map<String, ChatRoomDto> rooms = ChatRoomMap.getInstance().getChatRooms();

    /*
     SDP: WebRTC에서는 두 브라우저(혹은 클라이언트)가 영상, 오디오, 데이터 채널로 통신하려면 다음과 같은 정보를 서로 알아야 하고, 이 모든 정보를 담는 게 SDP이다.
	    - 내가 사용할 코덱 종류 (예: VP8, H264, Opus 등)
	    - 사용할 IP 주소와 포트
	    - 암호화 방식
	    - 오디오/비디오 여부
	    - 해상도/프레임레이트 제한 등
	 ICE: Peer간의 네트워크 연결 설정을 위한 프레임워크 (네트워크 연결을 위한 다양한 경로 (Candidate)를 찾고 연결을 확립)
     */

    /*
    [시그널링 타입]   [포함 정보]
    "offer"	        sdp 있음, candidate 없음
    "answer"	    sdp 있음, candidate 없음
    "ice"	        candidate 있음, sdp 없음
     */
    // Message types, used in signalling:
    // SDP Offer message
    private static final String MSG_TYPE_OFFER = "offer"; // 상대 피어로부터 연결 제안이 왔을 때 처리
    // SDP Answer message
    private static final String MSG_TYPE_ANSWER = "answer"; // 상대 피어가 제안에 응답했을 때 처리
    // New ICE Candidate message
    private static final String MSG_TYPE_ICE = "ice"; // ICE 후보 정보를 처리
    // Join room data message
    private static final String MSG_TYPE_JOIN = "join"; // 피어가 방에 참가할 때 처리
    // Leave room data message
    private static final String MSG_TYPE_LEAVE = "leave"; // 피어가 방을 떠날 때 처리

    // Signaling Server (WebSocket)의 연결이 끊어졌을 때 이벤트 처리
    @Override
    public void afterConnectionClosed (WebSocketSession session, CloseStatus status) { // WebSocketSession은 WebSocket 연결을 나타내는 객체
        log.info("[ws] Session has been closed with status [{} {}]", status, session);
    }

    // Signaling Server (WebSocket)가 연결되었을 때 이벤트 처리
    @Override
    public void afterConnectionEstablished (WebSocketSession session) {
        /*
         * 웹소켓이 연결되었을 때 클라이언트 쪽으로 메시지를 발송한다
         * 이때 이전 코드에서는 rooms.isEmpty()가 false를 전달한다. 이는 현재 room에 아무도 없다는 것을 의미하고 따라서 추가적인 ICE 요청을 하지 않도록 한다.
         *
         * 현재 채팅 코드에서는 chatRoom 내부의 userList 속에 user가 저장되기 때문에 rooms이 아닌 userList에 몇명이 있는지 확인해야 한다.
         * 따라서 클라이언트 (js) 쪽에서 ajax 요청을 통해 rooms가 아닌 userList에 몇명이 있는지 확인하고, 2명 이상인 경우에만 클라이언트 (js)에서 이와 관련된 변수를 true 가 되도록 변경하였다.
         *
         * 이렇게 true 상태가 되면 이후에 들어온 유저가 방안에 또 다른 유저가 있음을 확인하고,
         * P2P 연결을 시작한다.
         * */
        sendMessage(session, new WebSocketMessage("Server", MSG_TYPE_JOIN, Boolean.toString(!rooms.isEmpty()), null, null));
    }

    private void sendMessage (WebSocketSession session, WebSocketMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message); // 객체를 JSON 문자열로 변환 (직렬화)
            session.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            log.debug("An error occured: {}", e.getMessage());
        }
    }

    /**
     *  - 하기 handleTextMessage 메서드는 SockJs에서 전달받은 메시지 (TextMessage)를 수신하는 메서드이다. 해당 메서드를 기준으로 ICE와 SDP 교환이 일어난다.
     *  - 메서드가 실행되면서 userUUID와 roomID를 저장한다. 이후 전달받은 메시지의 타입에 따라서 시그널링 서버의 기능을 시작한다.
     */
    // 수신 메시지 처리
    @Override
    protected void handleTextMessage (WebSocketSession session, TextMessage textMessage) {
        // a message has been received
        try {
            // 전달받은 메시지
            // 소켓쪽에서는 socket.send 로 메시지를 발송한다 => 참고로 JSON 형식으로 변환해서 전달해온다
            WebSocketMessage message = objectMapper.readValue(textMessage.getPayload(), WebSocketMessage.class); // JSON 데이터를 객체로 변환
            log.debug("[ws] Message of {} type from {} received", message.getType(), message.getFrom());
            // 유저 uuid 와 roomID 를 저장
            String userUUID = message.getFrom(); // 유저 uuid
            String roomId = message.getData(); // roomId

            ChatRoomDto room;
            // 메시지 타입에 따라서 Signaling Server에서 하는 역할이 달라진다
            switch (message.getType()) {
                // 클라이언트에게서 받은 메시지 타입에 따른 signaling 프로세스
                case MSG_TYPE_OFFER:
                case MSG_TYPE_ANSWER:
                case MSG_TYPE_ICE:
                    Object candidate = message.getCandidate(); // ICE Candidate는 클라이언트 (SockJs에서 찾아서 Signaling 서버로 전송)
                    Object sdp = message.getSdp(); // SDP는 클라이언트 (SockJs에서 Signaling 서버로 전송)

                    // 삼항 연산자 적용
                    log.debug("[ws] Signal: {}",
                            candidate != null
                                    ? candidate.toString().substring(0, 64) // 로그에 출력할 데이터를 너무 길지 않게 잘라서 보여주기 위함
                                    : sdp.toString().substring(0, 64)); // 로그에 출력할 데이터를 너무 길지 않게 잘라서 보여주기 위함

                    ChatRoomDto roomDto = rooms.get(roomId);

                    if (roomDto != null) {
                        Map<String, WebSocketSession> clients = rtcChatService.getClients(roomDto);

                        /*
                         * Map은 직접적으로 순회 (iteration)가 안 된다. 대신 .entrySet() 메서드를 사용하면 key와 value를 한 쌍으로 묶은 객체들 (Map.Entry<~,~>)을 반환할 수 있다.
                         * String은 userUUID를 의미
                         * */
                        for (Map.Entry<String, WebSocketSession> client : clients.entrySet())  {
                            // 현재 사용자 자신이 아닌 경우에만 모든 유저에게 메세지 전송
                            if (!client.getKey().equals(userUUID)) {
                                // select the same type to resend signal
                                sendMessage(client.getValue(),
                                        new WebSocketMessage(
                                                userUUID,
                                                message.getType(),
                                                roomId,
                                                candidate,
                                                sdp));
                            }
                        }
                    }
                    break;
                // identify user and their opponent
                case MSG_TYPE_JOIN:
                    // message.data contains connected room id
                    log.debug("[ws] {} has joined Room: #{}", userUUID, message.getData());

                    room = ChatRoomMap.getInstance().getChatRooms().get(roomId);

                    // room 내부에 존재하는 userList에 유저 추가
                    rtcChatService.addClient(room, userUUID, session);

                    // 채팅방 입장 후 유저 카운트 + 1
                    chatServiceMain.plusUserCnt(roomId);

                    rooms.put(roomId, room);
                    break;
                case MSG_TYPE_LEAVE:
                    // message data contains connected room id
                    log.info("[ws] {} is going to leave Room: #{}", userUUID, message.getData());

                    // roomID 기준 채팅방 찾아오기
                    room = rooms.get(message.getData());

                    // room clients list 에서 해당 유저 삭제
                    // 1. room 에서 client List 를 받아와서 keySet 을 이용해서 key 값만 가져온 후 stream 을 사용해서 반복문 실행
                    Optional<String> client = rtcChatService.getClients(room).keySet().stream()
                            // 2. 이때 filter - 일종의 if문 -을 사용하는데 entry 에서 key 값만 가져와서 userUUID 와 비교한다
                            .filter(clientListKeys -> StringUtils.equals(clientListKeys, userUUID))
                            // 3. 하여튼 동일한 것만 가져온다
                            .findAny();

                    // 만약 client 의 값이 존재한다면 - Optional 임으로 isPersent 사용 , null  아니라면 - removeClientByName 을 실행
                    client.ifPresent(userID -> rtcChatService.removeClientByName(room, userID));

                    // 채팅방에서 떠날 시 유저 카운트 - 1
                    chatServiceMain.minusUserCnt(roomId);

                    log.debug("삭제 완료 [{}] ",client);
                    break;
                // something should be wrong with the received message, since it's type is unrecognizable
                default:
                    log.debug("[ws] Type of the received message {} is undefined!", message.getType());
                    // handle this if needed
            }
        } catch (IOException e) {
            log.debug("An error occured: {}", e.getMessage());
        }
    }

}
