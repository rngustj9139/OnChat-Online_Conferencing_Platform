package koo.online_education_platform.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter @Setter
public class ChatRoomMap {

    private static ChatRoomMap chatRoomMap = new ChatRoomMap(); // 정적 필드 (static), 객체를 생성하지 않아도 유일한 인스턴스에 접근 가능 (인스턴스를 오직 하나만 생성해서 사용) (static을 일반 변수에 붙이면, 그 변수는 객체마다 따로 존재하지 않고 클래스 전체에서 하나만 존재하게 된다. 즉, 모든 인스턴스가 값을 공유)
    private Map<String, ChatRoomDto> chatRooms = new LinkedHashMap<>(); // LinkedHashMap: 입력한 순서대로 Key-Value 쌍을 기억하는 HashMap, ChatRoomMap이 static 타입이기 때문에 싱글톤으로 유지됨

    private ChatRoomMap() {} // private 생성자: 외부에서 new 사용 불가

    public static ChatRoomMap getInstance() { // static 메서드는 객체를 생성하지 않고도 호출 가능
        return chatRoomMap;
    }

}
