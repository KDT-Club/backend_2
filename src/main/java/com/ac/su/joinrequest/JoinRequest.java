package com.ac.su.joinrequest;

import com.ac.su.clubmember.RequestStatus;
import com.ac.su.community.club.Club;
import com.ac.su.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "JoinRequest")
public class JoinRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="request_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Column
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @ManyToOne
    @JoinColumn(name="member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @ManyToOne
    @JoinColumn(name="club_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Club club;

    @Override
    public String toString() {
        return "JoinRequest{" +
                "id=" + id +
                ", introduction='" + introduction + '\'' +
                ", status=" + status +
                ", member=" + member +
                ", club=" + club +
                '}';
    }
}