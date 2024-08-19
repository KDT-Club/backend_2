package com.ac.su.notification;

import com.ac.su.member.Member;
import com.ac.su.member.MemberRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    @GetMapping("/getnotifications")
    public MessageFlagResponse getMessageFlag() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Member member = memberRepository.findByStudentId(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + currentUsername));

        List<Notification> unreadNotifications = notificationRepository.findByMemberAndReadFlagFalse(member);

        boolean hasUnreadMessages = !unreadNotifications.isEmpty();

        return new MessageFlagResponse(hasUnreadMessages ? "Y" : "N");
    }

    @Getter
    @Setter
    public static class MessageFlagResponse {
        private String messageFlag;

        public MessageFlagResponse(String messageFlag) {
            this.messageFlag = messageFlag;
        }
    }

    @GetMapping("/notificationsdetail/{memberId}")
    public List<NotificationResponse> getNotifications(@PathVariable Long memberId) {
        // 현재 로그인된 사용자의 ID 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Member currentMember = memberRepository.findByStudentId(currentUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + currentUsername));

        // 요청된 memberId가 현재 로그인된 사용자의 ID와 일치하는지 확인
        if (!currentMember.getId().equals(memberId)) {
            throw new IllegalArgumentException("Access denied for user with ID: " + memberId);
        }

        // 해당 사용자의 알림 가져오기
        List<Notification> notifications = notificationRepository.findByMember(currentMember);

        // 각 알림의 readFlag를 true로 설정하고 데이터베이스에 저장
        notifications.forEach(notification -> {
            if (!notification.isReadFlag()) {
                notification.setReadFlag(true);
                notificationRepository.save(notification); // 업데이트된 알림을 저장
            }
        });

        // NotificationResponse 객체로 변환하여 반환
        return notifications.stream()
                .map(notification -> new NotificationResponse(
                        notification.getId(),
                        notification.getMessage(),
                        notification.getCreatedAt(),
                        notification.getMember().getId(),
                        notification.isReadFlag()
                ))
                .collect(Collectors.toList());
    }

    @Getter
    @Setter
    public static class NotificationResponse {
        private Long notificationId;
        private String message;
        private String createdAt;
        private Long memberId;
        private boolean readFlag;

        public NotificationResponse(Long notificationId, String message, LocalDateTime createdAt, Long memberId, boolean readFlag) {
            this.notificationId = notificationId;
            this.message = message;
            this.createdAt = createdAt.toString();
            this.memberId = memberId;
            this.readFlag = readFlag;
        }
    }
}
