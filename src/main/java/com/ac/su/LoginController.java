package com.ac.su;

import com.ac.su.clubmember.MemberStatus;
import com.ac.su.member.Member;
import com.ac.su.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final MemberRepository memberRepository; //member 객체에 대한 입출력 함수
    private final PasswordEncoder passwordEncoder;

    //회원가입 기능 - /signup
    @PostMapping("/signup")
    ResponseEntity signup(
            String name,
            String username,
            String password,
            String department,
            String phone) {
        //유저가 보낸 학번, 학과, 이름 등을 저장
        Member member = new Member();
        member.setName(name);  //학생 이름 name
        member.setStudentId(username); //학번 프론트에서 username으로 보내줘여함!! -> 스프링 시큐리티 username
        var hashed_password = passwordEncoder.encode(password);
        member.setPassword(hashed_password); //비밀번호 해싱해서 저장
        member.setDepartment(department); // 학과
        //memberStatus컬럼은 clubMember 컬럼으로 옮겨짐 after 0708(화) 스프린트 회의
        member.setPhone(phone);
        memberRepository.save(member); //DB에 유저 정보 저장

        return ResponseEntity.ok(new ResponseMessage("성공")); //로그인 페이지로 이동 리다이렉트
    }

    //로그인 페이지로 이동
    @GetMapping("/login")
    String login() {
        // 로그인 페이지로 이동
        return "login";
    }

    @GetMapping("/mainPage")
    String mainpage(Authentication auth) {
        //feature/myclub 브랜치에 최신화한 main 브랜치 merge 해볼게
//        System.out.println(auth.getPrincipal());
//        System.out.println(auth.getName());
        System.out.println("Checking...");
        return "mainpage";
    }
}