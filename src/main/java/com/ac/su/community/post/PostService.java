package com.ac.su.community.post;


import com.ac.su.community.attachment.*;
import com.ac.su.community.board.Board;
import com.ac.su.community.board.BoardRepository;
import com.ac.su.community.report.Report;
import com.ac.su.community.postLike.PostLike;
import com.ac.su.community.postLike.PostLikeRepository;
import com.ac.su.community.report.ReportRepository;
import com.ac.su.member.Member;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentService attachmentService;
    private final PostLikeRepository postLikeRepository;
    private final ReportRepository reportRepository;  // 추가된 리포지토리

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

    // 신고 기능
    public void reportPost(Long postId, Optional<Member> member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId));
        // 사용자가 이 게시글을 신고했는지 확인
        if (reportRepository.existsByPostIdAndMemberId(postId, member.get().getId())) {
            throw new IllegalStateException("해당 게시글을 이미 신고하었습니다.");
        }

        // 새로운 신고 생성
        Report report = new Report();
        report.setPost(post);
        report.setMember(member.get());
        reportRepository.save(report);

        // 신고 수 체크
        long reportCount = reportRepository.countByPostId(postId);
        if (reportCount >= 5) {
            // 신고 횟수 5회 넘으면 게시글 차단
            blockPost(post);
        }

    }

    // 게시글 차단
    private void blockPost(Post post) {
        post.setPostType(PostType.BLOCKED);// 게시글 타입을 차단으로 변경
        postRepository.save(post);
    }
    // 좋아요 추가 및 좋아요 수 반환
    public long likePost(Long postId, Member member) {
        // 이미 좋아요를 눌렀는지 확인
        if (postLikeRepository.existsByPostIdAndMemberId(postId, member.getId())) {
            throw new IllegalStateException("이미 이 게시글에 좋아요를 눌렀습니다.");
        }

        // 새로운 좋아요 생성
        PostLike postLike = new PostLike();
        postLike.setPost(postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID: " + postId)));
        postLike.setMember(member);
        postLikeRepository.save(postLike);

        // 현재 게시글의 좋아요 수 반환
        return postLikeRepository.countByPostId(postId);
    }

    // 게시글의 좋아요 수 반환
    public long getLikeCount(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }
}
