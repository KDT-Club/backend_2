package com.ac.su.community.club;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {

    Optional<Club> findById(Long ClubId);

    Optional<Club> findByName(String ClubName); // 'clubName' 대신 'name'을 사용해야함 왜? 엔티티 클래스에 name이라고 했음

    List<Club> findByMemberId(Long memberId);
}
