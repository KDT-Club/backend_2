package com.ac.su.community.club;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ClubDTO {
    private Long clubId;
    private String clubName;
    private String description;
    private ClubType clubType;
    private Long member;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private String clubImgUrl;
    private String clubSlogan;
}

