package com.ac.su.presignedUrl.test;

import com.ac.su.community.club.Club;
import com.ac.su.community.club.ClubRepository;
import com.ac.su.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

}
