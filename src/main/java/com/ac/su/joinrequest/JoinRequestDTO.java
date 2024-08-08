package com.ac.su.joinrequest;

import com.ac.su.clubmember.RequestStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class JoinRequestDTO {
    private Long requestId;
    private String introduction;
    private RequestStatus status;
    private Long clubId;
    private Long memberId;
    private String memberImageurl;
    private String name;
    private String department;
    private String studentId;

    public static JoinRequestDTO toJoinRequestDTO(JoinRequest joinRequest){
        JoinRequestDTO joinRequestDTO = new JoinRequestDTO();
        joinRequestDTO.setRequestId(joinRequest.getId());
        joinRequestDTO.setIntroduction(joinRequest.getIntroduction());
        joinRequestDTO.setStatus(joinRequest.getStatus());
        joinRequestDTO.setClubId(joinRequest.getClub().getId());
        joinRequestDTO.setMemberId(joinRequest.getMember().getId());
        joinRequestDTO.setMemberImageurl(joinRequest.getMember().getMemberImageURL());
        joinRequestDTO.setName(joinRequest.getMember().getName());
        joinRequestDTO.setDepartment(joinRequest.getMember().getDepartment());
        joinRequestDTO.setStudentId(joinRequest.getMember().getStudentId());

        return joinRequestDTO;
    }
}