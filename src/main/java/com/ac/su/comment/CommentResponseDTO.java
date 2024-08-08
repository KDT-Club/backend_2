package com.ac.su.comment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseDTO {
    private Long commentId;
    private String memberName;
    private String content;
    private LocalDateTime createdAt;

    public CommentResponseDTO(Long commentId, String memberName, String content, LocalDateTime createdAt) {
        this.commentId = commentId;
        this.memberName = memberName;
        this.content = content;
        this.createdAt = createdAt;
    }
}