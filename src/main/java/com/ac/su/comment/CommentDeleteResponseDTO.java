package com.ac.su.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDeleteResponseDTO {
    private String message;

    public CommentDeleteResponseDTO(String message) {
        this.message = message;
    }
}
// 바보냐