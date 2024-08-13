package com.ac.su.presignedUrl.test;

import com.ac.su.community.club.Club;
import com.ac.su.community.club.ClubRepository;
import com.ac.su.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor //repository 등록시 필요
public class TestController {

    private final ClubRepository clubRepository;

    //모든 클럽 정보 불러옴
    @GetMapping("/test/clubs")
    public List<Club> getClubs() {
        List<Club> clubs = clubRepository.findAll();
        return clubs;
    }
    @GetMapping("/test/clubs2")
    public List<Club> getClubs2() {
        List<Club> clubs = clubRepository.customFindAll();
        return clubs;
    }

    //검색 기능 JPA contians 사용 테스트
    @GetMapping("/test/search")
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
