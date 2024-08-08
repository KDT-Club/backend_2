package com.ac.su.community.board;

import com.ac.su.community.attachment.Attachment;
import com.ac.su.community.attachment.AttachmentRepository;
import com.ac.su.community.club.Club;
import com.ac.su.community.club.ClubRepository;
import com.ac.su.community.post.Post;
import com.ac.su.community.post.PostRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final PostRepository postRepository;
    private final ClubRepository clubRepository;
    private final AttachmentRepository attachmentRepository;

    @GetMapping("/board/{board_id}/posts")
    public List<BoardDTO> getAllGeneralPost(@PathVariable Long board_id) {
        Board board = new Board();
        board.setId(board_id);
        var posts = postRepository.findByBoardId(board);

        return posts.stream()
                .map(post -> {
                    List<String> attachmentNames = attachmentRepository.findByPostId(post)
                            .stream()
                            .map(Attachment::getAttachmentName)
                            .collect(Collectors.toList());
                    return new BoardDTO(post.getId(), post.getTitle(), post.getContent(), post.getCreatedAt(), post.getMember().getId(), attachmentNames);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/board/{board_id}/posts/{post_id}")
    public PostResponse getPostDetails(@PathVariable Long board_id, @PathVariable Long post_id) {
        Optional<Post> postOptional = postRepository.findById(post_id);
        if (postOptional.isPresent() && postOptional.get().getBoardId().getId().equals(board_id)) {
            Post post = postOptional.get();
            List<String> attachmentNames = attachmentRepository.findByPostId(post)
                    .stream()
                    .map(Attachment::getAttachmentName)
                    .collect(Collectors.toList());
            return new PostResponse(post, attachmentNames);
        } else {
            throw new IllegalArgumentException("Post not found with id: " + post_id + " and board_id: " + board_id);
        }
    }

    @GetMapping("/clubs/{clubId}/board/2/posts")
    public List<BoardDTO> getAllNoticePosts(@PathVariable Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("Club not found with id: " + clubId));

        Board board = new Board();
        board.setId(2L);
        var posts = postRepository.findByBoardIdAndClubName(board, club.getName());

        return posts.stream()
                .map(post -> {
                    List<String> attachmentNames = attachmentRepository.findByPostId(post)
                            .stream()
                            .map(Attachment::getAttachmentName)
                            .collect(Collectors.toList());
                    return new BoardDTO(post.getId(), post.getTitle(), post.getContent(), post.getCreatedAt(), post.getMember().getId(), attachmentNames);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/board/3/clubs/{clubId}/posts")
    public List<BoardDTO> getAllActivityPosts(@PathVariable Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("Club not found with id: " + clubId));
        Board board = new Board();
        board.setId(3L);
        List<Post> posts = postRepository.findByBoardIdAndClubName(board, club.getName());

        return posts.stream()
                .map(post -> {
                    List<String> attachmentNames = attachmentRepository.findByPostId(post)
                            .stream()
                            .map(Attachment::getAttachmentName)
                            .collect(Collectors.toList());
                    return new BoardDTO(post.getId(), post.getTitle(), post.getContent(), post.getCreatedAt(), post.getMember().getId(), attachmentNames);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/clubs/{club_id}/board/{board_id}/posts/{post_id}")
    public PostResponse getClubBoardPostDetails(@PathVariable Long club_id, @PathVariable Long board_id, @PathVariable Long post_id) {
        Club club = clubRepository.findById(club_id)
                .orElseThrow(() -> new IllegalArgumentException("Club not found with id: " + club_id));

        Optional<Post> postOptional = postRepository.findById(post_id);
        if (postOptional.isPresent() && postOptional.get().getBoardId().getId().equals(board_id) && postOptional.get().getClubName().equals(club.getName())) {
            Post post = postOptional.get();
            List<String> attachmentNames = attachmentRepository.findByPostId(post)
                    .stream()
                    .map(Attachment::getAttachmentName)
                    .collect(Collectors.toList());
            return new PostResponse(post, attachmentNames);
        } else {
            throw new IllegalArgumentException("Post not found with post_id: " + post_id + ", board_id: " + board_id + ", and club_id: " + club_id);
        }
    }

    @GetMapping("/clubs/{clubId}/board/4/posts")
    public List<BoardDTO> getAllInternalPosts(@PathVariable Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("Club not found with id: " + clubId));
        Board board = new Board();
        board.setId(4L);
        List<Post> posts = postRepository.findByBoardIdAndClubName(board, club.getName());

        return posts.stream()
                .map(post -> {
                    List<String> attachmentNames = attachmentRepository.findByPostId(post)
                            .stream()
                            .map(Attachment::getAttachmentName)
                            .collect(Collectors.toList());
                    return new BoardDTO(post.getId(), post.getTitle(), post.getContent(), post.getCreatedAt(), post.getMember().getId(), attachmentNames);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("board/{board_id}/clubs/{club_id}/posts/{post_id}")
    public PostResponse getClubActivityPostDetails(@PathVariable Long club_id, @PathVariable Long board_id, @PathVariable Long post_id) {
        Club club = clubRepository.findById(club_id)
                .orElseThrow(() -> new IllegalArgumentException("Club not found with id: " + club_id));

        Optional<Post> postOptional = postRepository.findById(post_id);
        if (postOptional.isPresent() && postOptional.get().getBoardId().getId().equals(board_id) && postOptional.get().getClubName().equals(club.getName())) {
            Post post = postOptional.get();
            List<String> attachmentNames = attachmentRepository.findByPostId(post)
                    .stream()
                    .map(Attachment::getAttachmentName)
                    .collect(Collectors.toList());
            return new PostResponse(post, attachmentNames);
        } else {
            throw new IllegalArgumentException("Post not found with post_id: " + post_id + ", board_id: " + board_id + ", and club_id: " + club_id);
        }
    }

    @GetMapping("/postdetail/{postId}")
    public PostResponse getPostById(@PathVariable Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            List<String> attachmentNames = attachmentRepository.findByPostId(post)
                    .stream()
                    .map(Attachment::getAttachmentName)
                    .collect(Collectors.toList());
            return new PostResponse(post, attachmentNames);
        } else {
            throw new IllegalArgumentException("Post not found with id: " + postId);
        }
    }

    @Setter
    @Getter
    public class PostResponse {
        // Getters and setters
        private Post post;
        private List<String> attachmentNames;

        public PostResponse(Post post, List<String> attachmentNames) {
            this.post = post;
            this.attachmentNames = attachmentNames;
        }

    }
}
