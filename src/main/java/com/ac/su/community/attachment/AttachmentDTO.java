package com.ac.su.community.attachment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttachmentDTO {
    private Long id;
    private String attachmentName;
    private Long postId;
}
