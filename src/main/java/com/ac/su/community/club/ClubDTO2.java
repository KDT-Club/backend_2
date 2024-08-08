package com.ac.su.community.club;

import com.ac.su.member.Member;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ClubDTO2 {

    private Long clubId;
    private String clubName;
    private String description;
    private ClubType clubType;
    @ManyToOne
    @JoinColumn
    private Member member;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private String clubImgUrl;
    private String clubSlogan;
}

