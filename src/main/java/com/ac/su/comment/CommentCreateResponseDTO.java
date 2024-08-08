package com.ac.su.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateResponseDTO {
    private String message;

    public CommentCreateResponseDTO(String message) {
        this.message = message;
    }
}
