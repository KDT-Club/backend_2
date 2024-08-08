package com.ac.su.clubmember;

import com.ac.su.community.club.Club;
import com.ac.su.community.club.ClubRepository;
import com.ac.su.member.MemberRepository;
import com.ac.su.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClubMemberService {
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final ClubMemberRepository clubMemberRepository;

    // 동아리 id로 동아리 회원 전체 검색
    public List<ClubMemberDTO> findAllByClubId(Long clubId) {
        List<ClubMember> clubMemberList = clubMemberRepository.findByClubId(clubId);
        // ClubMember -> ClubMemberDTO로 변환
        List<ClubMemberDTO> clubMemberDTOList = new ArrayList<>();
        for (ClubMember clubMember : clubMemberList){
            clubMemberDTOList.add(ClubMemberDTO.toClubMemberDTO(clubMember));
        }
        return clubMemberDTOList;
    }

    // 멤버 id로 멤버 검색 후 상세 정보 출력
    public ClubMemberDTO findByMemberId(Long memberId, Long clubId) {
        ClubMember clubMember = clubMemberRepository.findById(new ClubMemberId(memberId, clubId)).orElseThrow(() -> new IllegalArgumentException("동아리에 가입되지 않은 회원입니다."));
        return ClubMemberDTO.toClubMemberDTO(clubMember);
    }

    // 회원 상태 수정
    public void changeStatus(Long memberId, Long clubId, MemberStatus status) {
        ClubMember clubMember = clubMemberRepository.findById(new ClubMemberId(memberId, clubId)).orElseThrow(() -> new IllegalArgumentException("동아리에 가입되지 않은 회원입니다."));
        clubMember.setStatus(status);
        memberRepository.save(clubMember.getMember());
    }

    // 회원 삭제
    public void deleteMember(Long memberId, Long clubId) {
        clubMemberRepository.deleteById(new ClubMemberId(memberId, clubId));
    }

    // 동아리 회원 존재 여부 검사
    public boolean existsById(Long memberId, Long clubId) {
        ClubMemberId clubMemberId = new ClubMemberId(memberId, clubId);
        return clubMemberRepository.existsById(clubMemberId);
    }

    // 동아리 회원의 등급 확인
    public MemberStatus getMemberStatus(ClubMemberId clubMemberId) {
        Optional<ClubMember> clubMember = clubMemberRepository.findById(clubMemberId);

        // 회원이 특정 동아리에 속해 있는지 확인하고 상태를 반환
        if (clubMember.isPresent()) {
            return clubMember.get().getStatus();
        } else {
            // 동아리에 속해 있지 않으면 예외 발생
            throw new IllegalArgumentException("회원이 동아리에 가입되지 않았습니다.");
        }
    }

    // 동아리 회장 위임
    public void delegatePresident(Long clubId) {
        // 동아리 회장 검색
        ClubMember clubPresident = clubMemberRepository.findByClubIdAndStatus(clubId, MemberStatus.CLUB_PRESIDENT);
        // 회장 -> 일반 회원
        clubPresident.setStatus(MemberStatus.CLUB_MEMBER);
        clubMemberRepository.save(clubPresident);
    }
}
