package com.ac.su.notification;

import com.ac.su.member.Member;
import com.ac.su.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    public void createNotification(Long memberId, String message) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + memberId));

        Notification notification = new Notification();
        notification.setMember(member);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setReadFlag(false);

        notificationRepository.save(notification);
    }
}