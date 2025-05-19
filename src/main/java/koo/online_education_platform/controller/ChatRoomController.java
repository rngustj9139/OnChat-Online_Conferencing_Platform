package koo.online_education_platform.controller;

import koo.online_education_platform.dto.ChatRoomDto;
import koo.online_education_platform.dto.ChatRoomMap;
import koo.online_education_platform.service.chatService.ChatServiceMain;
import koo.online_education_platform.service.social.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatServiceMain chatServiceMain;

    // 스프링 시큐리티의 로그인 유저 정보는 Security 세션의 PrincipalDetails 안에 담긴다
    // 정확히는 PrincipalDetails 내부에 ChatUser 객체가 담기고, 이것을 가져오면 된다.
    @GetMapping("/")
    public String goChatRoom (Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        model.addAttribute("list", chatServiceMain.findAllRoom());

        // principalDetails가 null이 아니라면 로그인 된 상태를 의미
        if (principalDetails != null) {
            // 세션에서 로그인 유저 정보를 가져옴
            model.addAttribute("user", principalDetails.getUser());
            log.info("user [{}] ", principalDetails);
        }

        // model.addAttribute("user", "hey");
        log.info("SHOW ALL ChatList {}", chatServiceMain.findAllRoom());

        return "roomlist";
    }

    // 채팅방 생성
    // 채팅방 생성 후 다시 / 로 redirect
    // Get 요청에서는, 요청 URL: /greet?name=Jun, 응답: Hello, Jun (URL에 쿼리스트링으로 정보가 노출 된다)
    // POST 요청에서는 요청 URL에 RequestParam 정보가 뜨지 않는다.
    @PostMapping("/chat/createroom")
    public String createRoom (@RequestParam("roomName") String name,
                              @RequestParam("roomPwd") String roomPwd,
                              @RequestParam("secretChk") String secretChk,
                              @RequestParam(value = "maxUserCnt", defaultValue = "2") String maxUserCnt,
                              @RequestParam("chatType") String chatType,
                              RedirectAttributes rttr) {
        // 매개변수 : 방 이름, 패스워드, 방 잠금 여부, 방 인원수
        ChatRoomDto room;
        room = chatServiceMain.createChatRoom(name, roomPwd, Boolean.parseBoolean(secretChk), Integer.parseInt(maxUserCnt), chatType);

        log.info("CREATE Chat Room [{}]", room);
        rttr.addFlashAttribute("roomName", room); //addAttribute(): URL에 쿼리스트링으로 붙여 전달 (?key=value), addFlashAttribute(): 세션에 잠깐 저장 → 다음 요청에서 자동 제거 (노출 X)

        return "redirect:/";
    }

    // 채팅방 입장 화면
    // 파라미터로 넘어오는 roomId를 확인 후 해당 roomId를 기준으로 채팅방을 찾아서 클라이언트를 chatroom으로 보낸다.
    @GetMapping("/chat/room")
    public String roomDetail (Model model, String roomId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        log.info("roomId {}", roomId);

        // principalDetails가 null이 아니라면 로그인 된 상태를 의미
        if (principalDetails != null) {
            // 세션에서 로그인 유저 정보를 가져옴
            model.addAttribute("user", principalDetails.getUser());
        }

        ChatRoomDto room = ChatRoomMap.getInstance().getChatRooms().get(roomId);
        model.addAttribute("room", room);

        if (ChatRoomDto.ChatType.MSG.equals(room.getChatType())) {
            return "chatroom";
        } else {
            model.addAttribute("uuid", UUID.randomUUID().toString()); // 제거 예정

            return "rtcroom";
        }
    }

    // 채팅방 비밀번호 확인
    @PostMapping("/chat/confirmPwd/{roomId}")
    @ResponseBody
    public boolean confirmPwd (@PathVariable String roomId, @RequestParam String roomPwd) {
        // 넘어온 roomId와 roomPwd를 이용해서 비밀번호 찾기
        // 찾아서 입력받은 roomPwd와 roomPwd와 비교해서 맞으면 true, 아니면 false 리턴
        return chatServiceMain.confirmPwd(roomId, roomPwd); // true 혹은 false 값은 roomlist.html의 ajax 함수에서 사용됨
    }

    // 채팅방 삭제
    @GetMapping("/chat/delRoom/{roomId}")
    public String delChatRoom (@PathVariable String roomId) {
        // roomId 기준으로 chatRoomMap에서 삭제 & 해당 채팅룸 안에 있는 사진 삭제
        chatServiceMain.delChatRoom(roomId);

        return "redirect:/";
    }

    // 유저 카운트
    @GetMapping("/chat/chkUserCnt/{roomId}")
    @ResponseBody
    public boolean chUserCnt (@PathVariable String roomId) {
        return chatServiceMain.chkRoomUserCnt(roomId); // true 혹은 false 값은 roomlist.html의 ajax 함수에서 사용됨
    }

}
