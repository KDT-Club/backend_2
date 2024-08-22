package com.ac.su.community.attachment;

import com.ac.su.community.post.Post;
import com.ac.su.community.post.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private PostRepository postRepository;

    // 첨부파일 저장 로직
    public AttachmentDTO saveAttachment(AttachmentDTO attachmentDTO) {
        Optional<Post> optionalPost = postRepository.findById(attachmentDTO.getPostId());
        if (optionalPost.isPresent()) {
            Attachment attachment = new Attachment();
            attachment.setAttachmentName(attachmentDTO.getAttachmentName());
            attachment.setPostId(optionalPost.get());
            Attachment savedAttachment = (Attachment) attachmentRepository.save(attachment);
            return new AttachmentDTO(savedAttachment.getId(), savedAttachment.getAttachmentName(), savedAttachment.getPostId().getId());
        }
        throw new IllegalArgumentException("Invalid Post ID");
    }

    // 첨부파일 삭제 로직
    public void deleteAttachment(Long postId, Long attachmentId) {
        // 해당 postId와 attachmentId로 첨부파일을 조회
        Optional<Attachment> optionalAttachment = attachmentRepository.findById(attachmentId);
        if (optionalAttachment.isPresent()) {
            Attachment attachment = optionalAttachment.get();
            // 첨부파일이 해당 게시물에 속하는지 확인
            if (attachment.getPostId().getId().equals(postId)) {
                attachmentRepository.delete(attachment); // 첨부파일 삭제
            } else {
                throw new IllegalArgumentException("Attachment does not belong to the given post");
            }
        } else {
            throw new IllegalArgumentException("Attachment not found with ID: " + attachmentId);
        }
    }
}
