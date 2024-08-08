package com.ac.su.community.post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateDto {
    private Long postId;
    private String title;
    private String content;
    private String attachment_flag; // EnumType.STRING 을 사용하므로 String 타입 사용
    private String attachment_name;
}
