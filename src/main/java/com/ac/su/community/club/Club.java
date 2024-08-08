package com.ac.su.community.club;

import com.ac.su.clubmember.ClubMember;
import com.ac.su.community.board.BoardType;
import com.ac.su.joinrequest.JoinRequest;
import com.ac.su.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "Club")
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="club_id")
    private Long id;

    @Column(length=20, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt; // 생성 날짜

    @Column
    private String clubImgUrl;

    @Column
    private String clubSlogan;

    @Column
    @Enumerated(EnumType.STRING)
    private ClubType clubType;

    // 동아리 회장 참조키
    @OneToOne
    @JsonIgnore
    @JoinColumn(name="member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    // 클럽 멤버와의 관계 설정 (cascade 옵션 추가)
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ClubMember> clubMembers = new ArrayList<>();

    // 가입 요청과의 관계 설정 (cascade 옵션 추가)
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<JoinRequest> joinRequests = new ArrayList<>();

    @Override
    public String toString() {
        return "Club{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", clubImgUrl='" + clubImgUrl + '\'' +
                ", clubSlogan='" + clubSlogan + '\'' +
                ", member=" + member +
                '}';
    }
}
