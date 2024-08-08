package com.ac.su.member;

import com.ac.su.community.club.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final ClubRepository clubRepository;

    @Override
    public UserDetails loadUserByUsername(String studentId) throws UsernameNotFoundException {
        var result = memberRepository.findByStudentId(studentId);
        System.out.println("loadUserByUsername에서 result:" + result);
        if (result.isEmpty()) {
            throw new UsernameNotFoundException("그런 아이디(학번) 없음");
        }
        var user = result.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("일반 유저"));

        var a = new CustonUser(user.getStudentId(), user.getPassword(), authorities);
        a.setId(user.getId());
        a.setName(user.getName());
        a.setDepartment(user.getDepartment());
//        a.setStatus(user.getStatus()); //MemberStatus 컬럼 ClubMember로 옮기는 것로 수정함
        a.setMemberImageURL(user.getMemberImageURL());
        a.setPhone(user.getPhone());
        a.setClub(user.getClub());

        return a;
    }
}