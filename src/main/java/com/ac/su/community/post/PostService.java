package com.ac.su.community.post;

import com.ac.su.community.attachment.Attachment;
import com.ac.su.community.attachment.AttachmentDTO;
import com.ac.su.community.attachment.AttachmentFlag;
import com.ac.su.community.attachment.AttachmentService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import com.ac.su.community.attachment.AttachmentRepository;
import com.ac.su.community.board.Board;
import com.ac.su.community.board.BoardRepository;
import com.ac.su.member.Member;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentService attachmentService;

    // 게시글 작성 (URL을 입력받아 처리)
    public void createPost(PostDTO postDTO, Member member, Long boardId, Long clubId) {
        Post post = new Post();
        post.setTitle(postDTO.getTitle());
        post.setContent(postDTO.getContent());
        post.setMember(member);
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid board Id: " + boardId));
        post.setBoardId(board);

        post.setAttachmentFlag(postDTO.getAttachment_flag());

        // clubId가 null이 아닐 때만 clubName을 설정
        if (clubId != null) {
            post.setClubName(postDTO.getClub_name());
        }

        // Post를 먼저 저장
        Post savedPost = postRepository.save(post);

        // 첨부 파일 URL이 있는 경우 Attachment 엔티티 생성 및 저장
        if (postDTO.getAttachment_flag() == AttachmentFlag.Y && postDTO.getAttachment_names() != null) {
            for (String attachmentUrl : postDTO.getAttachment_names()) {
                if (!attachmentUrl.isEmpty()) {
                    Attachment newAttachment = new Attachment();
                    newAttachment.setAttachmentName(attachmentUrl); // URL로 저장
                    newAttachment.setPostId(savedPost);

                    attachmentRepository.save(newAttachment);
                }
            }
        }
    }

    public List<Post> getPostsByMemberId(Long memberId) {
        return postRepository.findByMemberId(memberId);
    }

    public boolean deletePost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            postRepository.deleteById(postId);
            return true;
        }
        return false;
    }

    public PostResponseDto updatePost(Long postId, PostUpdateDto postUpdateDto) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setTitle(postUpdateDto.getTitle());
            post.setContent(postUpdateDto.getContent());
            if (postUpdateDto.getAttachment_flag() != null && !postUpdateDto.getAttachment_flag().isEmpty()) {
                try {
                    post.setAttachmentFlag(AttachmentFlag.valueOf(postUpdateDto.getAttachment_flag()));
                } catch (IllegalArgumentException e) {
                    return new PostResponseDto("에러남: Invalid AttachmentFlag value");

            }
            }
            postRepository.save(post);
            // attachment 코드 추가
            if (postUpdateDto.getAttachment_flag() != null && !postUpdateDto.getAttachment_flag().isEmpty() && !postUpdateDto.getAttachment_flag().equals("N")) {
                List<String> attachmentNames;
                if (postUpdateDto.getAttachment_name() != null && !postUpdateDto.getAttachment_name().isEmpty()) {
                    // 단일 문자열을 리스트로 변환
                    attachmentNames = Collections.singletonList(postUpdateDto.getAttachment_name());
                } else {
                    attachmentNames = Collections.emptyList();
                }
                if (!attachmentNames.isEmpty()) {
                    for (String attachmentName : attachmentNames) {
                        AttachmentDTO attachmentDTO = new AttachmentDTO(null, attachmentName, post.getId());
                        attachmentService.saveAttachment(attachmentDTO);
                    }
                }
            }
            return new PostResponseDto("성공");
        }
        return new PostResponseDto("에러남: Post not found");
    }

}
