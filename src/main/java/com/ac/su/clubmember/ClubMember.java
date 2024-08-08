package com.ac.su.clubmember;

import com.ac.su.community.club.Club;
import com.ac.su.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "ClubMember")
public class ClubMember {
    @EmbeddedId
    private ClubMemberId id;

    @MapsId("memberId")
    @ManyToOne
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @MapsId("clubId")
    @ManyToOne
    @JoinColumn(name = "club_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Club club;

    //MemberStatus 컬럼 ClubMember로 옮기는 것로 수정함
    @Column
    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Override
    public String toString() {
        return "ClubMember{" +
                "id=" + id +
                ", member=" + member +
                ", club=" + club +
                '}';
    }
}