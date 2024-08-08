package com.ac.su.community.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.ac.su.ResponseMessage;
import com.ac.su.clubmember.ClubMemberId;
import com.ac.su.clubmember.ClubMemberService;
import com.ac.su.clubmember.MemberStatus;
import com.ac.su.member.CustonUser;
import com.ac.su.member.Member;
import com.ac.su.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final MemberRepository memberRepository;
    private final ClubMemberService clubMemberService;

    // 현재 로그인한 사용자를 가져오는 메서드
    private Member getAuthenticatedMember(@AuthenticationPrincipal User user) {
        String studentId = user.getUsername();
        return memberRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user"));
    }

    // 자유 게시판 글 작성 처리 (URL을 입력받아 저장)
    @PostMapping("/board/1/posts")
    public ResponseEntity<ResponseMessage> createGeneralPost(@RequestBody PostDTO request, @AuthenticationPrincipal User user) {
        try {
            System.out.println(user);
            Member member = getAuthenticatedMember(user);
            postService.createPost(request, member, 1L, null);
            return ResponseEntity.ok(new ResponseMessage("게시글 작성 성공!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("게시글 작성 실패! 에러 메시지: " + e.getMessage()));
        }
    }


    // 동아리 활동 게시판 글 작성 처리
    @PostMapping("/board/3/club/{clubId}/posts")
    public ResponseEntity<ResponseMessage> createActivityPost(@PathVariable("clubId") Long clubId, @RequestBody PostDTO request, Authentication auth) {
        try {
            // 회원 상태 가져오기
            CustonUser user = (CustonUser) auth.getPrincipal();
            MemberStatus status = clubMemberService.getMemberStatus(new ClubMemberId(user.getId(), clubId));

            // 동아리 회장이 아닌 경우 접근 금지
            if (status != MemberStatus.CLUB_PRESIDENT) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("동아리 회장만 접근 가능합니다"));
            }
            Member member = memberRepository.getReferenceById(user.getId());

            postService.createPost(request, member, 3L, clubId);
            return ResponseEntity.ok(new ResponseMessage("게시글 작성 성공!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("게시글 작성 실패! 에러 메시지: " + e.getMessage()));
        }
    }

    // 동아리 공지사항 게시판 글 작성 처리
    @PostMapping("/club/{clubId}/board/2/posts")
    public ResponseEntity<ResponseMessage> createNoticePost(@PathVariable("clubId") Long clubId, @RequestBody PostDTO request, Authentication auth) {
        try {
            // 회원 상태 가져오기
            CustonUser user = (CustonUser) auth.getPrincipal();
            MemberStatus status = clubMemberService.getMemberStatus(new ClubMemberId(user.getId(), clubId));

            // 동아리 회장이 아닌 경우 접근 금지
            if (status != MemberStatus.CLUB_PRESIDENT) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseMessage("동아리 회장만 접근 가능합니다"));
            }
            Member member = memberRepository.getReferenceById(user.getId());

            postService.createPost(request, member, 2L, clubId);
            return ResponseEntity.ok(new ResponseMessage("게시글 작성 성공!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("게시글 작성 실패! 에러 메시지: " + e.getMessage()));
        }
    }

    // 동아리 내부 자유게시판 글 작성 처리
    @PostMapping("/club/{clubId}/board/4/posts")
    public ResponseEntity<ResponseMessage> createInternalPost(@PathVariable Long clubId, @RequestBody PostDTO request, Authentication auth) {
        try {
            // 회원 상태 가져오기
            CustonUser user = (CustonUser) auth.getPrincipal();
            MemberStatus status = clubMemberService.getMemberStatus(new ClubMemberId(user.getId(), clubId));

            Member member = memberRepository.getReferenceById(user.getId());

            postService.createPost(request, member, 4L, clubId);
            return ResponseEntity.ok(new ResponseMessage("게시글 작성 성공!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("게시글 작성 실패! 에러 메시지: " + e.getMessage()));
        }
    }

    // 회원 ID로 게시물 조회
    @GetMapping("/posts/{memberId}")
    public List<Post> getPostsByMemberId(@PathVariable Long memberId) {
        return postService.getPostsByMemberId(memberId);
    }

    // 게시물 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        boolean isDeleted = postService.deletePost(postId);
        if (isDeleted) {
            return ResponseEntity.ok("{\"message\":\"게시물 삭제 성공\"}");
        } else {
            return ResponseEntity.status(400).body("{\"message\":\"게시물 삭제 실패\"}");
        }
    }

    // 게시물 수정
    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long postId,
                                      @RequestBody PostUpdateDto postUpdateDto) {
        PostResponseDto response = postService.updatePost(postId, postUpdateDto);
        return ResponseEntity.ok(response);

    }
}
