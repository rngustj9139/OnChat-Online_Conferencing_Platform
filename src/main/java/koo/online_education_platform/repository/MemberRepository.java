package koo.online_education_platform.repository;

import koo.online_education_platform.entity.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<ChatUser, Long> {
}
