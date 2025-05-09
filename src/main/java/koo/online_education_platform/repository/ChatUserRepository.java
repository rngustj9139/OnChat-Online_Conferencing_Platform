package koo.online_education_platform.repository;

import koo.online_education_platform.dto.ChatUserDto;
import koo.online_education_platform.entity.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {

    Optional<ChatUser> findByNickName (String nickName);

}
