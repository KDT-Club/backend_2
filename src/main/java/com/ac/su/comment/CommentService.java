package com.ac.su.comment;

import com.ac.su.community.post.Post;
import com.ac.su.community.post.PostRepository;
import com.ac.su.config.RedisLockService;
import com.ac.su.member.Member;
import com.ac.su.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    private final RedisLockService redisLockService;

    private final StringRedisTemplate redisTemplate;

    public CommentService(RedisLockService redisLockService, StringRedisTemplate redisTemplate) {
        this.redisLockService = redisLockService;
        this.redisTemplate = redisTemplate;
    }

    public CommentCreateResponseDTO createComment(Long postId, CommentCreateRequestDTO commentCreateRequestDTO) {
        Long memberId = commentCreateRequestDTO.getMemberId();
        // 댓글 작성을 위한 락 키 생성
        String lockKey = "commentLock:" + postId + ":" + memberId;

        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();

        // Redis에 키가 존재하는지 확인
        Boolean isLocked = valueOps.get(lockKey) != null;
        // 만약 키가 존재한다면 중복된 댓글 요청이라고 예외를 발생
        if (Boolean.TRUE.equals(isLocked)) {
            throw new IllegalStateException("중복된 댓글 요청입니다. 잠시 후 다시 시도하세요.");
        }

        // Redis에 키를 설정하고 2초 후에 자동으로 만료되도록 설정
        valueOps.set(lockKey, "lock", 2, TimeUnit.SECONDS);


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