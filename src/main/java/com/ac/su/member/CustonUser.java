package com.ac.su.member;

import com.ac.su.community.club.Club;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@Setter
public class CustonUser extends User {
    private Long id;
    private String department;
    //    private MemberStatus status; // MemberStatus 컬럼 ClubMember로 옮기는 것로 수정함

    private String memberImageURL;
    private String name;
    private String phone;
    private Club club;


    public CustonUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }
}
