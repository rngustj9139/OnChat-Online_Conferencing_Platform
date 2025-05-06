//package koo.online_education_platform.repository;
//
//import koo.online_education_platform.dto.ChatRoomDto;
//import koo.online_education_platform.service.fileService.FileService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Required;
//import org.springframework.stereotype.Repository;
//
//import javax.annotation.PostConstruct;
//import java.util.*;
//
//@Repository
//@Slf4j
//@RequiredArgsConstructor
//public class ChatRepository {
//
//    private Map<String, ChatRoomDto> chatRoomMap;
//    private final FileService fileService; // 채팅방 삭제에 따른 채팅방의 사진 삭제를 위한 fileService 주입
//
//    @PostConstruct
//    private void init() {
//        chatRoomMap = new LinkedHashMap<>();
//    }
//
//    // 전체 채팅방 조회
//    public List<ChatRoomDto> findAllRoom() {
//        // 채팅방 생성 순서를 최근순으로 반환
//        List chatRooms = new ArrayList<>(chatRoomMap.values()); // List: 인터페이스 (추상체), ArrayList: 클래스 (구현체)
//        Collections.reverse(chatRooms);
//
//        return chatRooms;
//    }
//
//    // roomID 기준으로 채팅방 찾기
//    public ChatRoomDto findRoomById(String roomId) {
//        return chatRoomMap.get(roomId);
//    }
//
//    // roomName 로 채팅방 만들기
//    public ChatRoomDto createChatRoom(String roomName, String roomPwd, boolean secretChk, int maxUserCnt) {
//        // roomName 와 roomPwd 로 chatRoom 빌드 후 return
//        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
//                .roomId(UUID.randomUUID().toString())
//                .roomName(roomName)
//                .roomPwd(roomPwd) // 채팅방 패스워드
//                .secretChk(secretChk) // 채팅방 잠금 여부
//                .userList(new HashMap<String, String>())
//                .userCount(0) // 채팅방 참여 인원수
//                .maxUserCnt(maxUserCnt) // 최대 인원수 제한
//                .build();
//
//        // map 에 채팅룸 아이디와 만들어진 채팅룸을 저장
//        chatRoomMap.put(chatRoomDto.getRoomId(), chatRoomDto);
//
//        return chatRoomDto;
//    }
//
//    // 채팅방 인원 + 1
//    public void plusUserCnt(String roomId) {
//        ChatRoomDto room = chatRoomMap.get(roomId);
//        room.setUserCount(room.getUserCount() + 1);
//    }
//
//    // 채팅방 인원 - 1
//    public void minusUserCnt(String roomId){
//        ChatRoomDto room = chatRoomMap.get(roomId);
//        room.setUserCount(room.getUserCount() - 1);
//    }
//
//    // maxUserCnt 에 따른 채팅방 입장 여부
//    public boolean chkRoomUserCnt(String roomId){
//        ChatRoomDto room = chatRoomMap.get(roomId);
//
//        log.info("참여인원 확인 [{}, {}]", room.getUserCount(), room.getMaxUserCnt());
//
//        if (room.getUserCount() + 1 > room.getMaxUserCnt()) {
//            return false;
//        }
//
//        return true;
//    }
//
//    // 채팅방 유저 리스트에 유저 추가
//    public String addUser(String roomId, String userName) {
//        ChatRoomDto room = chatRoomMap.get(roomId);
//        String userUUID = UUID.randomUUID().toString();
//
//        // 타입 캐스팅을 통해 userList에 값 추가
//        ((HashMap<String, String>) room.getUserList()).put(userUUID, userName);
//
//        return userUUID;
//    }
//
//    // 채팅방 유저 이름 중복 확인
//    public String isDuplicateName(String roomId, String username) {
//        ChatRoomDto room = chatRoomMap.get(roomId);
//        String tmp = username;
//
//        // 만약 userName이 중복이라면 랜덤한 숫자를 붙임
//        // 랜덤한 숫자를 붙였을 때 getUserlist 내부에 존재하는 닉네임이라면 다시 해당 작업 반복 수행
//        while (room.getUserList().containsValue(tmp)) {
//            int ranNum = (int) (Math.random() * 100) + 1;
//            tmp = username + ranNum; // String에 int 값을 더하는 경우 자바가 자동으로 문자열로 변환 수행
//        }
//
//        return tmp;
//    }
//
//    // 채팅방에서 유저 삭제
//    public void delUser(String roomId, String userUUID) {
//        ChatRoomDto room = chatRoomMap.get(roomId);
//        room.getUserList().remove(userUUID);
//    }
//
//    // 채팅방 userName 조회
//    public String getUserName(String roomId, String userUUID) {
//        ChatRoomDto room = chatRoomMap.get(roomId);
//
//        return (String) room.getUserList().get(userUUID);
//    }
//
//    // 채팅방 전체 userList 조회
//    public ArrayList<String> getUserList(String roomId) {
//        ArrayList<String> list = new ArrayList<>();
//
//        ChatRoomDto room = chatRoomMap.get(roomId);
//
//        // 해쉬맵을 순차 접근하여 value 값만 뽑아내고, list에 저장 후 return
//        room.getUserList().forEach((key, value) -> list.add((String) value));
//
//        return list;
//    }
//
//    // 채팅방 비밀번호 조회
//    public boolean confirmPwd(String roomId, String roomPwd) {
//        return roomPwd.equals(chatRoomMap.get(roomId).getRoomPwd());
//    }
//
//    // 채팅방 삭제
//    public void delChatRoom(String roomId) {
//        try {
//            // 채팅방 삭제
//            chatRoomMap.remove(roomId);
//            // 채팅방 내부에 존재하는 파일 삭제
//            fileService.deleteFileDir(roomId);
//
//            log.info("삭제 완료 roomId : {}", roomId);
//        } catch (Exception e) { // 만약 예외 발생시 에러 로그를 확인
//            log.error(e.getMessage());
//        }
//    }
//
//}
