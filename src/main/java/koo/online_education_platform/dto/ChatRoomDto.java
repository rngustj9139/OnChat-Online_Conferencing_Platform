package koo.online_education_platform.dto;

import com.sun.istack.NotNull;

import java.util.Map;

public class ChatRoomDto {

    @NotNull
    private String roomId; // 채팅방 아이디

    private String roomName; // 채팅방 이름
    private int userCount; // 채팅방 인원수
    private int maxUserCnt; // 채팅방 최대 인원 제한
    private String roomPwd; // 채팅방 삭제시 필요한 pwd
    private boolean secretChk; // 채팅방 잠금 여부
    public enum ChatType{  // 화상 채팅, 문자 채팅
        MSG, RTC
    }
    private ChatType chatType; //  채팅 타입 여부
    private Map<String, ?> userList; // ChatRoomDto 클래스는 하나로 가되 서비스를 나누었음

}
