package com.ac.su.notification;
import com.ac.su.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberAndReadFlagFalse(Member member);
    List<Notification> findByMember(Member member);
}
