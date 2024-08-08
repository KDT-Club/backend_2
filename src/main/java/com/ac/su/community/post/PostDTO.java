package com.ac.su.community.post;

import com.ac.su.community.attachment.AttachmentFlag;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostDTO {
    private String title;
    private String content;
    private AttachmentFlag attachment_flag;
    private List<String> attachment_names;
    private String club_name;
}
