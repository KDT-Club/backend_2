package com.ac.su.member;

import lombok.Data;

@Data
public class MemberDTO {


    private Long id;
    private String name;
    private String department;
    private String studentId;
    private String password;
    private String phone;
    private String memberImageURL;

}
