package com.ac.su.community.club;


import com.ac.su.ResponseMessage;
import com.ac.su.clubmember.*;
import com.ac.su.member.CustonUser;
import com.ac.su.clubmember.ClubMemberRepository;
import com.ac.su.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class ClubController {

    private final ClubRepository clubRepository; //Club 객체에 대한 입출력 함수
    private final ClubMemberRepository clubMemberRepository; //ClubMember 객체에 대한 입출력 함수
    private final ClubService clubService;
    private final ClubMemberService clubMemberService;

    // 설명: 모든 동아리 정보를 불러온다
    @GetMapping("/clubs")
    public ResponseEntity<?> getClubs(@RequestParam(name = "memberId", required = false) Long memberId) {
        if (memberId == null) {
            // memberId가 없을 때 /clubs
            return clubService.getAllClubs();
        } else {
            // memberId가 있을 때 /clubs?memberId=2
            return clubService.getClubsByMemberId(memberId);
        }
    }

    // 내 동아리 목록 (동아리 메뉴 초기 페이지)
    // /clubs?memberId={memberId}
    @GetMapping("/clubs/{clubName}")
    public ClubDTO2 getClubByName(@PathVariable String clubName) {
        Optional<Club> a = clubRepository.findByName(clubName); //간단해서 서비스 레이어로 분리안했음

        //DTO에 클럽 객체랑 멤버를 담아주자
        ClubDTO2 clubDTO2 = new ClubDTO2();
        clubDTO2.setClubId(a.get().getId());
        clubDTO2.setClubName(a.get().getName());
        clubDTO2.setClubSlogan(a.get().getClubSlogan());
        clubDTO2.setClubImgUrl(a.get().getClubImgUrl());
        clubDTO2.setClubType(a.get().getClubType());
        clubDTO2.setDescription(a.get().getDescription());

        //멤버 객체도 담아주자
        Member member = new Member();
        member.setId(a.get().getMember().getId());
        member.setName(a.get().getMember().getName());
        member.setPhone(a.get().getMember().getPhone());
        member.setMemberImageURL(a.get().getMember().getMemberImageURL());
        member.setStudentId(a.get().getMember().getStudentId());
        clubDTO2.setMember(member);
        System.out.println("aaaa: " + clubDTO2);
        return clubDTO2;
    }

    // 동아리 생성
    @PostMapping("/clubs/create/{memberId}")
    public ResponseEntity<?> createClub(@PathVariable Long memberId, @RequestBody ClubDTO clubDTO) {
        Club createdClub = clubService.createClub(clubDTO,memberId);
        if (createdClub == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("클럽 생성 실패");
        } else {
            return  ResponseEntity.status(HttpStatus.OK).body("클럽 생성 성공");
        }
    }

    // 동아리 정보 불러옴(GET)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/clubs/{clubId}/changeClubInfo")
    public ResponseEntity<?> getClubInfo(@PathVariable("clubId") Long clubId,
                                         Authentication auth) {
        // 회원 상태 가져오기
        CustonUser user = (CustonUser) auth.getPrincipal();
        MemberStatus status = clubMemberService.getMemberStatus(new ClubMemberId(user.getId(), clubId));

        // 동아리 회장이 아닌 경우 접근 금지
        if (status != MemberStatus.CLUB_PRESIDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("동아리 회장만 접근 가능합니다"));
        }

        // 동아리 정보 불러옴
        ClubInfoDTO clubInfoDTO = clubService.getClubByClubId(clubId);
        return ResponseEntity.ok(clubInfoDTO);
    }

    // 동아리 상태 수정(POST)
    @PostAuthorize("isAuthenticated()")
    @PostMapping("/clubs/{clubId}/changeClubInfo")
    public ResponseEntity<?> changeStatus(@PathVariable("clubId") Long clubId,
                                          @RequestBody ClubInfoDTO clubInfoDTO) {
        // 현재 저장되어 있는 동아리 정보 받아옴
        ClubInfoDTO existingClubInfo = clubService.getClubByClubId(clubId);

        // 사용자가 수정한 값이 없는 경우 기존 정보 그대로 반영
        String clubName = existingClubInfo.getClubName();
        if (!clubInfoDTO.getClubName().isEmpty()) {
            clubName = clubInfoDTO.getClubName();
        }

        String clubSlogan = existingClubInfo.getClubSlogan();
        if (!clubInfoDTO.getClubSlogan().isEmpty()) {
            clubSlogan = clubInfoDTO.getClubSlogan();
        }

        String description = existingClubInfo.getDescription();
        if (!clubInfoDTO.getDescription().isEmpty()) {
            description = clubInfoDTO.getDescription();
        }

        String clubImgUrl = existingClubInfo.getClubImgUrl();
        if (!clubInfoDTO.getClubImgUrl().isEmpty()) {
            clubImgUrl = clubInfoDTO.getClubImgUrl();
        }

        // 동아리 정보 변경
        clubService.changeClubInfo(clubId, clubName, clubSlogan, description, clubImgUrl);
        return ResponseEntity.ok(new ResponseMessage("동아리 정보가 성공적으로 변경되었습니다."));
    }

    // 동아리 삭제
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/clubs/{clubId}/deleteClub")
    public ResponseEntity<?> deleteMember(@PathVariable("clubId") Long clubId,
                                          Authentication auth) {
        // 회원 상태 가져오기
        CustonUser user = (CustonUser) auth.getPrincipal();
        MemberStatus status = clubMemberService.getMemberStatus(new ClubMemberId(user.getId(), clubId));

        // 동아리 회장이 아닌 경우 접근 금지
        if (status != MemberStatus.CLUB_PRESIDENT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("동아리 회장만 접근 가능합니다"));
        }

        // 동아리 삭제
        clubService.deleteClub(clubId);
        return ResponseEntity.ok("동아리를 삭제했습니다.");
    }
}
