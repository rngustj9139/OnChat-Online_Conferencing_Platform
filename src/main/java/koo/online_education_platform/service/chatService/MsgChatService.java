package koo.online_education_platform.service.chatService;

import koo.online_education_platform.dto.ChatRoomDto;
import koo.online_education_platform.dto.ChatRoomMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class MsgChatService {

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

        room.setUserList(new HashMap<String, String>());

        // msg 타입이면 ChatType.MSG
        room.setChatType(ChatRoomDto.ChatType.MSG);

        // map에 채팅룸 아이디와 만들어진 채팅룸을 저장
        ChatRoomMap.getInstance().getChatRooms().put(room.getRoomId(), room);

        return room;
    }

    // 채팅방 유저 리스트에 유저 추가
    public String addUser (Map<String, ChatRoomDto> chatRoomMap, String roomId, String userName) {
        ChatRoomDto room = chatRoomMap.get(roomId);
        String userUUID = UUID.randomUUID().toString();

        HashMap<String, String> userList = (HashMap<String, String>) room.getUserList();
        userList.put(userUUID, userName);

        return userUUID;
    }

    // 채팅방 내 유저 이름 중복 확인
    public String isDuplicateName (Map<String, ChatRoomDto> chatRoomMap, String roomId, String username) {
        ChatRoomDto room = chatRoomMap.get(roomId);
        String tmp = username;

        // 만약 userName이 중복이라면 랜덤한 숫자를 붙임
        // 이때 랜덤한 숫자를 붙였을 때 getUserlist 내부에 이미 존재하는 닉네임이라면 다시 랜덤한 숫자 붙이는 작업 재수행
        while (room.getUserList().containsValue(tmp)) {
            int ranNum = (int) (Math.random() * 100) + 1;
            tmp = username + ranNum; // 문자형 + 정수형 연산 발생시 자바가 자동으로 정수형 타입을 문자형 타입으로 변환시킴
        }

        return tmp;
    }

    // 채팅방 userName 조회
    public String findUserNameByRoomIdAndUserUUID (Map<String, ChatRoomDto> chatRoomMap, String roomId, String userUUID) {
        ChatRoomDto room = chatRoomMap.get(roomId);
        return (String) room.getUserList().get(userUUID);
    }

    // 채팅방 전체 userList 조회
    public ArrayList<String> getUserList (Map<String, ChatRoomDto> chatRoomMap, String roomId) {
        ArrayList<String> list = new ArrayList<>();

        ChatRoomDto room = chatRoomMap.get(roomId);

        // 해쉬맵 컴포넌트를 차례로 확인하는 반복문 수행
        // value 값만 뽑아내서 list에 저장 후 return
        room.getUserList().forEach((key, value) -> list.add((String) value));

        return list;
    }

    // 채팅방 특정 유저 삭제
    public void delUser (Map<String, ChatRoomDto> chatRoomMap, String roomId, String userUUID) {
        ChatRoomDto room = chatRoomMap.get(roomId);
        room.getUserList().remove(userUUID);
    }

}
