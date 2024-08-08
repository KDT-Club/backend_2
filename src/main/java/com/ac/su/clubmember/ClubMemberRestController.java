package com.ac.su.clubmember;

import com.ac.su.ResponseMessage;
import com.ac.su.community.club.ClubService;
import com.ac.su.member.CustonUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clubs")
public class ClubMemberRestController {
    private final ClubMemberService clubMemberService;
    private final ClubMemberRepository clubMemberRepository;
    private final ClubService clubService;

    // 동아리 id에 따른 동아리 회원 리스트 출력
    @GetMapping("/{clubId}/clubMember")
    public ResponseEntity<?> clubMemberList(@PathVariable("clubId") Long clubId) {
        List<ClubMemberDTO> clubMemberDTOList = clubMemberService.findAllByClubId(clubId);
        return ResponseEntity.ok(clubMemberDTOList);
    }

    // 회원 상세 정보
    @GetMapping("/{clubId}/clubMember/{memberId}")
    public ResponseEntity<?> memberDetail(@PathVariable("memberId") Long memberId,
                                          @PathVariable("clubId") Long clubId) {
        ClubMemberDTO clubMemberDTO = clubMemberService.findByMemberId(memberId, clubId);
        return ResponseEntity.ok(clubMemberDTO);
    }

    // 회원 상태 수정 (status는 현재 사용자의 상태, changeStatus는 변경할 회원의 상태)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{clubId}/clubMember/{memberId}/changeStatus")
    public ResponseEntity<?> changeStatus(@PathVariable("memberId") Long memberId,
                                          @PathVariable("clubId") Long clubId,
                                          @RequestBody MemberStatusDTO statusDTO,
                                          Authentication auth) {
        // 회원 상태 가져오기
        CustonUser user = (CustonUser) auth.getPrincipal();
        MemberStatus status = clubMemberService.getMemberStatus(new ClubMemberId(user.getId(), clubId));

        // 동아리 회장이 아닌 경우 접근 금지
        if (status != MemberStatus.CLUB_PRESIDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("동아리 회장만 접근 가능합니다"));
        }

        // 잘못된 회원 등급을 입력했을때
        MemberStatus changeStatus;
        try {
            changeStatus = MemberStatus.valueOf(statusDTO.getChangeStatus());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage("잘못된 회원 등급"));
        }

        // 회원 상태 수정
        // 회원 상태를 회장으로 변경할시 기존에 있던 회장이 일반회원으로 변경됨
        if(changeStatus == MemberStatus.CLUB_PRESIDENT) {
            clubMemberService.delegatePresident(clubId);
        }
        clubMemberService.changeStatus(memberId, clubId, changeStatus);
        clubService.changePresident(clubId, memberId);
        return ResponseEntity.ok("회원 등급이 성공적으로 변경되었습니다.");
    }

    // 회원 삭제
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{clubId}/clubMember/{memberId}/deleteMember")
    public ResponseEntity<?> deleteMember(@PathVariable("memberId") Long memberId,
                                          @PathVariable("clubId") Long clubId,
                                          Authentication auth) {
        // 회원 상태 가져오기
        CustonUser user = (CustonUser) auth.getPrincipal();
        MemberStatus status = clubMemberService.getMemberStatus(new ClubMemberId(user.getId(), clubId));

        // 동아리 회장이 아닌 경우 접근 금지
        if (status != MemberStatus.CLUB_PRESIDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("동아리 회장만 접근 가능합니다"));
        }

        // 회원 삭제
        // 삭제하려는 회원이 회장일시 삭제 불가능
        MemberStatus targetStatus = clubMemberService.getMemberStatus(new ClubMemberId(memberId, clubId));
        if (targetStatus == MemberStatus.CLUB_PRESIDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("동아리 회장 위임 후 탈퇴 가능합니다."));
        } else {
            clubMemberService.deleteMember(memberId, clubId);
            return ResponseEntity.ok("회원을 탈퇴시켰습니다.");
        }
    }

    // 회원이 자기 의지를 가지고 탈퇴
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{clubId}/withdrawClub")
    public ResponseEntity<?> deleteMember(@PathVariable("clubId") Long clubId,
                                          Authentication auth) {
        // 회원 상태 가져오기
        CustonUser user = (CustonUser) auth.getPrincipal();
        MemberStatus status = clubMemberService.getMemberStatus(new ClubMemberId(user.getId(), clubId));

        // 동아리 회장일시 위임 후 탈퇴 가능
        if (status == MemberStatus.CLUB_PRESIDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("동아리 회장 위임 후 탈퇴 가능합니다."));
        }

        // 회원 탈퇴
        clubMemberService.deleteMember(user.getId(), clubId);
        return ResponseEntity.ok("동아리를 성공적으로 탈퇴했습니다.");
    }
}