package koo.online_education_platform.controller;

import koo.online_education_platform.dto.ChatDto;
import koo.online_education_platform.dto.ChatRoomMap;
import koo.online_education_platform.repository.ChatRepository;
import koo.online_education_platform.service.chatService.ChatServiceMain;
import koo.online_education_platform.service.chatService.MsgChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.ArrayList;

/**
 * - 채팅을 수신 (sub)하고, 송신 (pub)하기 위한 Controller
 * - @MessageMapping : 해당 어노테이션은 클라이언트가 발송 (pub)한 메시지가 STOMP에 도착하는 엔드포인트를 나타내는 어노테이션이다. 여기서 "/chat/enterUser" 로 되어있지만 실제로는 앞에 "/pub" 가 생략되어있다라고 생각하면 된다. 즉 클라이언트가 "/pub/chat/enterUser"로 메시지를 발송하면 @MessageMapping에 의해서 아래의 해당 어노테이션이 달린 메서드가 실행된다.
 * - convertAndSend() : 해당 메서드는 매개변수로 각각 메시지의 도착 지점 (Topic, 도착 엔드포인트)과 객체를 넣어준다. 이를 통해서 인자로 들어온 객체를 Message 객체로 변환해서 해당 도작 지점 (Topic, 도착 엔드포인트)을 sub (구독) 하고 있는 모든 사용자에게 메시지를 보내주게 된다.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations template; // 아래에서 사용되는 convertAndSend 메서드를 사용하기 위해서 선언, convertAndSend는 객체를 인자로 넘겨주면 자동으로 Message 객체로 변환 후 Topic으로 전송한다.
    private final MsgChatService msgChatService;
    private final ChatServiceMain chatServiceMain;

    // MessageMapping을 통해 WebSocket & STOMP로 들어오는 메시지를 처리한다.
    // 이때 클라이언트에서는 /pub/chat/enterUser로 요청하게 되고 이것을 controller가 받아서 처리한다. (resources/static/js/chatroom/socket.js 내용 확인)
    // 처리가 완료되면 /sub/chat/room/{roomId}로 메시지가 전송된다. (resources/static/js/chatroom/socket.js 내용 확인)
    @MessageMapping("/chat/enterUser")
    public void enterUser (@Payload ChatDto chat, SimpMessageHeaderAccessor headerAccessor) { // @Payload 어노테이션을 통해 클라이언트가 보낸 JSON 메세지를 ChatDto 객체로 변환 (매핑)
        // 채팅방 유저 + 1
        chatServiceMain.plusUserCnt(chat.getRoomId());

        // 채팅방에 유저 추가 및 UserUUID 반환
        String userUUID = msgChatService.addUser(ChatRoomMap.getInstance().getChatRooms(), chat.getRoomId(), chat.getSender());

        // 반환 결과를 STOMP Session에 저장 (웹소켓을 쓰면, 클라이언트(브라우저)는 서버와 "계속 연결된 상태"가 된다. 그런데 이 연결 상태에서는 "누가 접속했는지", "어느 채팅방에 있는지"와 같은 정보가 기본적으로 자동으로 따라오지 않는다. 그래서 직접 저장해줘야 함 => 나중에 서버가 다시 이 유저의 정보를 확인할 수 있게 된다.)
        headerAccessor.getSessionAttributes().put("userUUID", userUUID);
        headerAccessor.getSessionAttributes().put("roomId", chat.getRoomId());

        chat.setMessage(chat.getSender() + "님 입장!!");
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
    }

    // 특정 유저의 메시지 송신
    @MessageMapping("/chat/sendMessage")
    public void sendMessage (@Payload ChatDto chat) {
        log.info("CHAT {}", chat);
        chat.setMessage(chat.getMessage());
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
    }

    // 유저 퇴장 시에는 EventListener를 통해서 유저 퇴장을 확인
    @EventListener
    public void webSocketDisconnectListener (SessionDisconnectEvent event) { // @EventListener로 SessionDisconnectEvent를 자동 감지
        log.info("DisConnEvent {}", event);

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage()); // SessionDisconnectEvent에서 STOMP 헤더를 꺼내기 위해 래핑함

        // STOMP Session에 존재하였으며 이벤트를 발생시킨 userUUID 와 roomId를 확인해서 채팅방 유저 리스트와 채팅방에서 해당 유저를 삭제
        String userUUID = (String) headerAccessor.getSessionAttributes().get("userUUID");
        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");

        log.info("headAccessor {}", headerAccessor);

        // 채팅방 유저 - 1
        chatServiceMain.minusUserCnt(roomId);

        // 채팅방 유저 리스트에서 UUID 유저 닉네임 조회 및 리스트에서 유저 삭제
        String username = msgChatService.findUserNameByRoomIdAndUserUUID(ChatRoomMap.getInstance().getChatRooms(), roomId, userUUID);
        msgChatService.delUser(ChatRoomMap.getInstance().getChatRooms(), roomId, userUUID);

        if (username != null) {
            log.info("User Disconnected : " + username);

            // builder 어노테이션 활용
            ChatDto chat = ChatDto.builder()
                    .type(ChatDto.MessageType.LEAVE)
                    .sender(username)
                    .message(username + "님 퇴장!!")
                    .build();

            template.convertAndSend("/sub/chat/room/" + roomId, chat);
        }
    }

    // 채팅에 참여한 유저 리스트 반환
    @GetMapping("/chat/userlist")
    @ResponseBody
    public ArrayList<String> userList (String roomId) {
        return msgChatService.getUserList(ChatRoomMap.getInstance().getChatRooms(), roomId);
    }

    // 채팅에 참여한 유저 닉네임 중복 확인
    @GetMapping("/chat/duplicateName")
    @ResponseBody
    public String isDuplicateName (@RequestParam("roomId") String roomId, @RequestParam("username") String username) {
        // 유저 이름 확인
        String userName = msgChatService.isDuplicateName(ChatRoomMap.getInstance().getChatRooms(), roomId, username);
        log.info("동작확인 {}", userName);

        return userName;
    }

}
