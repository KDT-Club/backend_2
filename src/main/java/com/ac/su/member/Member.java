package com.ac.su.member;

import com.ac.su.clubmember.ClubMember;
import com.ac.su.comment.Comment;
import com.ac.su.community.club.Club;
import com.ac.su.community.post.Post;
import com.ac.su.joinrequest.JoinRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Member")
public class Member {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long id;

    @Column
    private String name;

    @Column
    private String department;

    @Column
    private String studentId;

    @Column
    private String password;

    @Column
    private String memberImageURL;

    @Column
    private String phone;

    @JsonIgnore
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Club club;

    // 클럽 멤버와의 관계 설정 (cascade 옵션 추가)
    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClubMember> clubMembers = new ArrayList<>();

    // 가입 요청과의 관계 설정 (cascade 옵션 추가)
    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JoinRequest> joinRequests = new ArrayList<>();

    // 댓글과의 관계 설정 (cascade 옵션 추가)
    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> Comment = new ArrayList<>();

    // 게시글과의 관계 설정 (cascade 옵션 추가)
    @JsonIgnore
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> Post = new ArrayList<>();

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", studentId=" + studentId +
                ", password='" + password + '\'' +
                ", phone=" + phone +
                ", memberImageURL='" + memberImageURL + '\'' +
                '}';
    }
}