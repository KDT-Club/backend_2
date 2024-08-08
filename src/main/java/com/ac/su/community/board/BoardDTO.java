package com.ac.su.community.board;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class BoardDTO {
    private Long postId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Long memberId;
    private List<String> attachmentNames; // 첨부 파일 이름 목록 추가

    public BoardDTO(Long postId, String title, String content, LocalDateTime createdAt, Long memberId, List<String> attachmentNames) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.memberId = memberId;
        this.attachmentNames = attachmentNames; // 초기화
    }
}
