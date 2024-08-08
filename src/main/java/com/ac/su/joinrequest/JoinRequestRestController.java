package com.ac.su.joinrequest;

import com.ac.su.ResponseMessage;
import com.ac.su.clubmember.ClubMemberId;
import com.ac.su.clubmember.ClubMemberService;
import com.ac.su.clubmember.MemberStatus;
import com.ac.su.member.CustonUser;
import com.ac.su.member.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clubs")
public class JoinRequestRestController {
    private final JoinRequestService joinRequestService;
    private final ClubMemberService clubMemberService;
    private final JoinRequestRepository joinRequestRepository;

    // 동아리 id에 따른 동아리 지원서 리스트 출력
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{clubId}/joinRequest")
    public ResponseEntity<?> joinRequestList(
            @PathVariable("clubId") Long clubId,
            Authentication auth) {
        // 회원 상태 가져오기
        CustonUser user = (CustonUser) auth.getPrincipal();
        MemberStatus status = clubMemberService.getMemberStatus(new ClubMemberId(user.getId(), clubId));

        // 동아리 회장이 아닌 경우 접근 금지
        if (status != MemberStatus.CLUB_PRESIDENT) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("동아리 회장만 접근 가능합니다"));
        }

        // 동아리 지원서 리스트 출력
        List<JoinRequestDTO> joinRequestDTOList = joinRequestService.findRequestByClubId(clubId);
        return ResponseEntity.ok(joinRequestDTOList);
    }

    // 동아리 지원서 상세 정보
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{clubId}/joinRequest/{requestId}")
    public ResponseEntity<?> requestDetail(
            @PathVariable("clubId") Long clubId,
            @PathVariable("requestId") Long requestId,
            Authentication auth) {
        // 회원 상태 가져오기
        CustonUser user = (CustonUser) auth.getPrincipal();
        MemberStatus status = clubMemberService.getMemberStatus(new ClubMemberId(user.getId(), clubId));

        // 동아리 회장이 아닌 경우 접근 금지
        if (status != MemberStatus.CLUB_PRESIDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("동아리 회장만 접근 가능합니다"));
        }

        // 동아리 지원서 상세 정보 출력
        JoinRequestDTO joinRequestDTO = joinRequestService.findByRequestId(requestId);
        return ResponseEntity.ok(joinRequestDTO);
    }

    // 가입 승인
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{clubId}/joinRequest/{requestId}/approveRequest")
    public ResponseEntity<?> approveRequest(
            @PathVariable("requestId") Long requestId,
            @PathVariable("clubId") Long clubId,
            Authentication auth) {
        // 회원 상태 가져오기
        CustonUser user = (CustonUser) auth.getPrincipal();
        MemberStatus status = clubMemberService.getMemberStatus(new ClubMemberId(user.getId(), clubId));

        // 동아리 회장이 아닌 경우 접근 금지
        if (status != MemberStatus.CLUB_PRESIDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("동아리 회장만 접근 가능합니다"));
        }

        Long targetMemberId = joinRequestService.getMemberIdByRequestId(requestId);
        // 가입 승인
        joinRequestService.approveRequest(requestId, clubId, targetMemberId);
        return ResponseEntity.ok("가입을 승인했습니다.");
    }

    // 가입 거절
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{clubId}/joinRequest/{requestId}/denyRequest")
    public ResponseEntity<?> denyRequest(
            @PathVariable("requestId") Long requestId,
            @PathVariable("clubId") Long clubId,
            Authentication auth) {
        // 회원 상태 가져오기
        CustonUser user = (CustonUser) auth.getPrincipal();
        MemberStatus status = clubMemberService.getMemberStatus(new ClubMemberId(user.getId(), clubId));

        // 동아리 회장이 아닌 경우 접근 금지
        if (status != MemberStatus.CLUB_PRESIDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("동아리 회장만 접근 가능합니다"));
        }

        // 가입 거절
        joinRequestService.denyRequest(requestId);
        return ResponseEntity.ok("가입을 거절했습니다.");
    }
}
