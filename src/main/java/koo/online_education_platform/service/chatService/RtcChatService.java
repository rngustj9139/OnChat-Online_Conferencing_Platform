package koo.online_education_platform.service.chatService;

import koo.online_education_platform.dto.ChatRoomDto;
import koo.online_education_platform.dto.ChatRoomMap;
import koo.online_education_platform.dto.WebSocketMessage;
import koo.online_education_platform.service.fileService.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Service
@Slf4j
public class RtcChatService {

    public ChatRoomDto createChatRoom (String roomName, String roomPwd, boolean secretChk, int maxUserCnt) {
        // roomName과 roomPwd로 chatRoom 빌드 후 return
        ChatRoomDto room = ChatRoomDto.builder()
                .roomId(UUID.randomUUID().toString())
                .roomName(roomName)
                .roomPwd(roomPwd) // 채팅방 패스워드
                .secretChk(secretChk) // 채팅방 잠금 여부
                .userCount(0) // 채팅방 참여 인원수
                .maxUserCnt(maxUserCnt) // 최대 인원수 제한
                .build();

        room.setUserList(new HashMap<String, WebSocketSession>());

        // rtc 타입이면 ChatType.RTC
        room.setChatType(ChatRoomDto.ChatType.RTC);

        ChatRoomMap.getInstance().getChatRooms().put(room.getRoomId(), room);

        return room;
    }

    public Map<String, WebSocketSession> getClients (ChatRoomDto room) {
        return (Map<String, WebSocketSession>) room.getUserList();
    }

    public Map<String, WebSocketSession> addClient (ChatRoomDto room, String name, WebSocketSession session) {
        Map<String, WebSocketSession> userList = (Map<String, WebSocketSession>) room.getUserList();
        userList.put(name, session);

        return userList;
    }

    // userList에서 클라이언트 삭제
    public void removeClientByName (ChatRoomDto room, String userUUID) {
        room.getUserList().remove(userUUID);
    }

    // 유저 카운터 return
    public boolean findUserCount (WebSocketMessage webSocketMessage){ // WebSocketMessage: 커스텀 클래스
        ChatRoomDto room = ChatRoomMap.getInstance().getChatRooms().get(webSocketMessage.getData());
        log.info("ROOM COUNT : [{} ::: {}]", room.toString(), room.getUserList().size());

        return room.getUserList().size() > 1;
    }

}
