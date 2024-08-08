package com.ac.su.joinrequest;

import com.ac.su.ResponseMessage;
import com.ac.su.clubmember.*;
import com.ac.su.community.club.Club;
import com.ac.su.community.club.ClubRepository;
import com.ac.su.member.CustonUser;
import com.ac.su.member.Member;
import com.ac.su.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JoinRequestService {
    private final JoinRequestRepository joinRequestRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final ClubMemberRepository clubMemberRepository;

    public ResponseEntity<ResponseMessage> applyToClub(String clubName, JoinRequestController.ApplicationRequest request, Authentication auth) {
        // 인증되지 않은 사용자
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("로그인 필요"));
        }
        // 동아리를 찾을 수 없는 경우
        Optional<Club> clubOptional = clubRepository.findByName(clubName);
        if (clubOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("현재 그런 동아리 없음"));
        }

        //JoinRequest컬럼에 club 필드를 위한 club 객체 생성
        var club = new Club();
        club.setId(clubOptional.get().getId()); //club 객체의 Id 필드를 현재 지원 중인 "클럽이름"으로 저장

        CustonUser user = (CustonUser) auth.getPrincipal();
        //JoinRequest컬럼에 member 필드를 위한 club 객체 생성
        var member = new Member();
        member.setId(user.getId()); //member 객체의 Id 필드를 현재 로그인 유저의 id로 저장

        // 새로운 가입 신청 생성
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setIntroduction(request.getMotivation());
        joinRequest.setStatus(RequestStatus.WAITING); // 지원 시에는 "지원상태"를 "WAITING"로 설정
        joinRequest.setMember(member);
        joinRequest.setClub(club);

        joinRequestRepository.save(joinRequest); // JoinRequest

        // 성공적인 응답
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("가입신청이 완료!"));
    }

    // 동아리 id로 동아리 지원서 전체 검색
    public List<JoinRequestDTO> findRequestByClubId(Long clubId) {
//        List<JoinRequest> joinRequestList = joinRequestRepository.findByClubIdAndStatus(clubId, RequestStatus.WAITING);
        List<JoinRequest> joinRequestList = joinRequestRepository.findByClubId(clubId);
        // 동아리 지원서가 한 개도 없을 때 빈 리스트 반환
        if (joinRequestList.isEmpty()) {
            return new ArrayList<>();
        }
        // JoinRequest -> JoinRequestDTO로 변환
        List<JoinRequestDTO> joinRequestDTOList = new ArrayList<>();
        for (JoinRequest joinRequest : joinRequestList) {
            joinRequestDTOList.add(JoinRequestDTO.toJoinRequestDTO(joinRequest));
        }
        return joinRequestDTOList;
    }

    // 지원서 id로 동아리 지원서 검색 후 상세 정보 출력
    public JoinRequestDTO findByRequestId(Long requestId) {
        JoinRequest joinRequest = joinRequestRepository.findById(requestId).orElseThrow();
        return JoinRequestDTO.toJoinRequestDTO(joinRequest);
    }

    // 가입 승인
    public void approveRequest(Long requestId, Long clubId, Long memberId) {
//        JoinRequest joinRequest = joinRequestRepository.findById(requestId).orElseThrow();
//        // 지원서 상태 WAITING -> APPROVED
//        joinRequest.setStatus(RequestStatus.APPROVED);
//        joinRequestRepository.save(joinRequest);
        // 가입 승인 후 DB에서 지원서 제거
        joinRequestRepository.deleteById(requestId);

        Club club = clubRepository.findById(clubId).orElseThrow();
        Member member = memberRepository.findById(memberId).orElseThrow();
        ClubMember clubMember = new ClubMember();

        // 회원을 clubMember 테이블에 추가
        clubMember.setClub(club);
        clubMember.setMember(member);
        clubMember.setId(new ClubMemberId(clubId, memberId));
        clubMember.setStatus(MemberStatus.CLUB_MEMBER);
        clubMemberRepository.save(clubMember);
    }

    // 가입 거절
    public void denyRequest(Long requestId) {
        joinRequestRepository.deleteById(requestId);
    }

    // 지원서를 작성한 회원 id 검색하는 메소드
    public Long getMemberIdByRequestId(Long requestId) {
        JoinRequest joinRequest = joinRequestRepository.findById(requestId).orElseThrow();
        return joinRequest.getMember().getId();
    }
}