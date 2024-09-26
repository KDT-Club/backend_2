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
    // 동아리 정보 가져오기
    @GetMapping("/clubs/{clubName}")
    public ResponseEntity<?> getClubByName(@PathVariable String clubName) {
        Optional<Club> clubOptional = clubRepository.findByName(clubName);

        // Optional 값이 없으면 404 응답 반환
        if (clubOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Club with name " + clubName + " not found");
        }

        Club club = clubOptional.get();

        // DTO 생성
        ClubDTO2 clubDTO2 = new ClubDTO2();
        clubDTO2.setClubId(club.getId());
        clubDTO2.setClubName(club.getName());
        clubDTO2.setClubSlogan(club.getClubSlogan());
        clubDTO2.setClubImgUrl(club.getClubImgUrl());
        clubDTO2.setClubType(club.getClubType());
        clubDTO2.setDescription(club.getDescription());

        // Member 객체 설정
        Member member = new Member();
        member.setId(club.getMember().getId());
        member.setName(club.getMember().getName());
        member.setPhone(club.getMember().getPhone());
        member.setMemberImageURL(club.getMember().getMemberImageURL());
        member.setStudentId(club.getMember().getStudentId());
        clubDTO2.setMember(member);

        // 성공 시 200 OK 응답
        return ResponseEntity.ok(clubDTO2);
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

    //검색 기능
    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String clubName) {
        // 1. 검색어가 비어 있거나 공백만 있는 경우, 잘못된 요청으로 간주하여 400 Bad Request 응답을 반환
        if (clubName == null || clubName.trim().isEmpty()) {
            // 메시지를 포함한 JSON 응답을 반환
            Map<String, String> response = new HashMap<>();
            response.put("message", "검색어를 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 2. 검색어를 포함하는 클럽 리스트를 데이터베이스에서 검색
            List<Club> clubs = clubRepository.findByNameContains(clubName);

            // 3. 검색 결과가 비어 있는 경우, 404 Not Found 응답을 반환
            if (clubs.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "검색 결과가 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // 4. 검색 결과가 있는 경우, 정상적인 200 OK 응답과 함께 결과 반환
            return ResponseEntity.ok(clubs);

        } catch (Exception e) {
            // 5. 검색 중 예기치 않은 오류가 발생한 경우, 500 Internal Server Error 응답을 반환
            Map<String, String> response = new HashMap<>();
            response.put("message", "서버에서 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
