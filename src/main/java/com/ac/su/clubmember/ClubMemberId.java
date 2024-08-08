package com.ac.su.clubmember;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

// pk(멤버 id, 동아리 id) 클래스
@Embeddable
public class ClubMemberId implements Serializable {
    private Long memberId;
    private Long clubId;

    public ClubMemberId() {}

    public ClubMemberId(Long memberId, Long clubId) {
        this.memberId = memberId;
        this.clubId = clubId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getClubId() {
        return clubId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClubMemberId that = (ClubMemberId) o;
        return Objects.equals(memberId, that.memberId) && Objects.equals(clubId, that.clubId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, clubId);
    }
}