package com.ac.su.community.club;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.*;

@Data
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ClubInfoDTO {
    private String clubName;
    private String description;
    private String clubImgUrl;
    private String clubSlogan;

    // 동아리 수정 기능에서 표시될 내용입니다
    public static ClubInfoDTO toClubInfoDTO(Club club) {
        ClubInfoDTO clubInfoDTO = new ClubInfoDTO();
        clubInfoDTO.setClubName(club.getName());
        clubInfoDTO.setClubSlogan(club.getClubSlogan());
        clubInfoDTO.setDescription(club.getDescription());
        clubInfoDTO.setClubImgUrl(club.getClubImgUrl());

        return clubInfoDTO;
    }
}
