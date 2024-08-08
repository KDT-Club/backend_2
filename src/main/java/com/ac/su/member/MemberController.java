package com.ac.su.member;

import com.ac.su.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {

    @Autowired
    private MemberService memberService;

    private final MemberRepository memberRepository; //member 객체에 대한 입출력 함수

    //메인페이지로 이동
    @GetMapping("/")
    public ResponseEntity<ResponseMessage> test() {
        return ResponseEntity.ok(new ResponseMessage("성공"));
    }

    @GetMapping("/getUserId")
    public ResponseEntity getUserId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            // 인증되지 않은 경우 HTTP 401 (UNAUTHORIZED) 상태 코드와 메시지를 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("로그인 필요"));
        }
        else {
            //로그인한 유저의 고유 id 번호 봔환
            CustonUser user = (CustonUser) auth.getPrincipal();
            String userId = String.valueOf(user.getId());
            return ResponseEntity.ok(new ResponseMessage(userId));
        }
    }


    // 멤버 불러오기
    @GetMapping("members/{memberId}")
    public ResponseEntity<Member> getMember(@PathVariable Long memberId) {
        Optional<Member> memberOptional = memberService.getMemberById(memberId);
        if (memberOptional.isPresent()) {
            return ResponseEntity.ok(memberOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 멤버 정보 수정
    @PostMapping("members/{memberId}")
    // id랑 dto 값 받아서 저장 - 부분 수정이라도 전체 값을 받아야 함.
    public ResponseEntity<String> updateMember(@PathVariable Long memberId, @RequestBody MemberDTO memberDTO) {
        Member updatedMember = memberService.updateMember(memberId, memberDTO);
        if (updatedMember != null) {
            return ResponseEntity.ok("수정 성공!");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 멤버 삭제
    @DeleteMapping("members/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable Long memberId) {
        Optional<Member> memberOptional = memberService.getMemberById(memberId);
        if (memberOptional.isPresent()) {
            memberService.deleteMember(memberId);
            return ResponseEntity.ok("계정 삭제 성공!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("계정 삭제 실패!");
        }
    }
}
