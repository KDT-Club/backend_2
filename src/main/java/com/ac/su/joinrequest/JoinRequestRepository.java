package com.ac.su.joinrequest;

import com.ac.su.clubmember.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long> {
    // 동아리 id로 지원서 검색
    List<JoinRequest> findByClubId(Long clubId);

    // 동아리 id, 지원서 상태로 지원서 검색
    List<JoinRequest> findByClubIdAndStatus(Long clubId, RequestStatus status);

    // 동아리 id로 동아리 지원서 삭제
    void deleteByClubId(Long clubId);
}