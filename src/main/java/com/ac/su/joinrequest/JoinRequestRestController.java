package com.ac.su.joinrequest;

import com.ac.su.ResponseMessage;
import com.ac.su.community.club.Club;
import com.ac.su.community.club.ClubRepository;
import com.ac.su.clubmember.ClubMemberId;
import com.ac.su.clubmember.ClubMemberService;
import com.ac.su.clubmember.MemberStatus;
import com.ac.su.member.CustonUser;
import com.ac.su.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clubs")
public class JoinRequestRestController {
    private final JoinRequestService joinRequestService;
    private final ClubMemberService clubMemberService;
    private final NotificationService notificationService;
    private final ClubRepository clubRepository; // ClubRepository 추가

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{clubId}/joinRequest/{requestId}/approveRequest")
    public ResponseEntity<?> approveRequest(
            @PathVariable("requestId") Long requestId,
            @PathVariable("clubId") Long clubId,
            Authentication auth) {
        CustonUser user = (CustonUser) auth.getPrincipal();
        MemberStatus status = clubMemberService.getMemberStatus(new ClubMemberId(user.getId(), clubId));

        if (status != MemberStatus.CLUB_PRESIDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("동아리 회장만 접근 가능합니다"));
        }

        Long targetMemberId = joinRequestService.getMemberIdByRequestId(requestId);
        joinRequestService.approveRequest(requestId, clubId, targetMemberId);

        // 클럽 이름 가져오기
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("Club not found with id: " + clubId));
        String clubName = club.getName();

        // Notification 생성
        String message = clubName + "에 가입이 승인되었습니다";
        notificationService.createNotification(targetMemberId, message);

        return ResponseEntity.ok("가입을 승인했습니다.");
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{clubId}/joinRequest/{requestId}/denyRequest")
    public ResponseEntity<?> denyRequest(
            @PathVariable("requestId") Long requestId,
            @PathVariable("clubId") Long clubId,
            Authentication auth) {
        CustonUser user = (CustonUser) auth.getPrincipal();
        MemberStatus status = clubMemberService.getMemberStatus(new ClubMemberId(user.getId(), clubId));

        if (status != MemberStatus.CLUB_PRESIDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("동아리 회장만 접근 가능합니다"));
        }

        Long targetMemberId = joinRequestService.getMemberIdByRequestId(requestId);
        joinRequestService.denyRequest(requestId);

        // 클럽 이름 가져오기
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("Club not found with id: " + clubId));
        String clubName = club.getName();

        // Notification 생성
        String message = clubName + "에 가입이 거부되었습니다";
        notificationService.createNotification(targetMemberId, message);

        return ResponseEntity.ok("가입을 거절했습니다.");
    }
}
