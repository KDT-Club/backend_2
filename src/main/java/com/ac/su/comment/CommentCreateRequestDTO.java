package com.ac.su.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateRequestDTO {
    private Long memberId;
    private String content;
}