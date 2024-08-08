package com.ac.su.comment;

import com.ac.su.community.post.Post;
import com.ac.su.community.post.PostRepository;
import com.ac.su.member.Member;
import com.ac.su.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    public CommentCreateResponseDTO createComment(Long postId, CommentCreateRequestDTO commentCreateRequestDTO) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Member member = memberRepository.findById(commentCreateRequestDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Comment comment = new Comment();
        comment.setContent(commentCreateRequestDTO.getContent());
        comment.setPost(post);
        comment.setMember(member);

        commentRepository.save(comment);

        return new CommentCreateResponseDTO("성공");
    }

    public List<CommentResponseDTO> getCommentsByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        List<Comment> comments = commentRepository.findByPost(post);
        return comments.stream()
                .map(comment -> new CommentResponseDTO(
                        comment.getId(),
                        comment.getMember().getName(),
                        comment.getContent(),
                        comment.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public CommentDeleteResponseDTO deleteComment(Long postId, Long commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getPost().getId().equals(postId)) {
            throw new RuntimeException("Comment does not belong to the given post");
        }

        commentRepository.delete(comment);

        return new CommentDeleteResponseDTO("성공");
    }

    public boolean updateComment(Long postId, Long commentId, CommentUpdateDto commentUpdateDto) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            if (comment.getPost().getId().equals(postId)) {
                comment.setContent(commentUpdateDto.getContent());
                commentRepository.save(comment);
                return true;
            }
        }
        return false;
    }
}