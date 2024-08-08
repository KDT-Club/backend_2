package com.ac.su.clubmember;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubMemberRepository extends JpaRepository<ClubMember, ClubMemberId> {
    List<ClubMember> findByMemberId(Long memberId);

    //     findByMember(Long memberId);
    List<ClubMember> findByClubId(Long clubId);

    boolean existsById(ClubMemberId clubMemberId);

    // 특정 등급의 동아리 회원을 검색하는 메소드
    ClubMember findByClubIdAndStatus(Long clubId, MemberStatus status);

    // 동아리 id로 동아리 회원 삭제하는 메소드
    void deleteByClubId(Long clubId);

    boolean existsByMemberIdAndStatus(Long memberId, MemberStatus status);
}

