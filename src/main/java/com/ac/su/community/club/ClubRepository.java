package com.ac.su.community.club;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {

    Optional<Club> findById(Long ClubId);

    Optional<Club> findByName(String ClubName); // 'clubName' 대신 'name'을 사용해야함 왜? 엔티티 클래스에 name이라고 했음

    List<Club> findByMemberId(Long memberId);

    //JPQL 문법 사용  112ms -> 33ms
    @Query(value = "SELECT c FROM Club c JOIN FETCH c.member")
    List<Club> customFindAll();

    // SQL 조인 문법 사용 112ms -> 239ms
    @Query(value = "SELECT c.* FROM club c INNER JOIN member m ON c.member_id = m.member_id", nativeQuery = true)
    List<Club> customFindAll2();

}
