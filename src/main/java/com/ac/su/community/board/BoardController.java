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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final PostRepository postRepository;
    private final ClubRepository clubRepository;
    private final AttachmentRepository attachmentRepository;

    // 캐싱을 위한 어노테이션. 이 메서드의 결과를 캐시에 저장하고,
    // 동일한 인자로 호출될 때 캐시에서 결과를 가져옴
    @Cacheable(
            cacheNames = "getBoards", // 캐시의 이름. 다른 캐시와 구분하기 위해 사용됨
            key = "'boards:board_id:' + #board_id", // 캐시의 키를 정의. 여기서는 페이지와 사이즈를 포함한 문자열로 키를 생성함
            cacheManager = "boardCacheManager" // 사용할 캐시 매니저를 지정함. RedisCacheConfig에서 정의한 캐시 매니저를 사용
    )
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
            List<Map<String, Object>> attachments = attachmentRepository.findByPostId(post)
                    .stream()
                    .map(attachment -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("attachmentId", attachment.getId());
                        map.put("attachmentName", attachment.getAttachmentName());
                        return map;
                    })
                    .collect(Collectors.toList());
            return new PostResponse(post, attachments);
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
            List<Map<String, Object>> attachments = attachmentRepository.findByPostId(post)
                    .stream()
                    .map(attachment -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("attachmentId", attachment.getId());
                        map.put("attachmentName", attachment.getAttachmentName());
                        return map;
                    })
                    .collect(Collectors.toList());
            return new PostResponse(post, attachments);
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
            List<Map<String, Object>> attachments = attachmentRepository.findByPostId(post)
                    .stream()
                    .map(attachment -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("attachmentId", attachment.getId());
                        map.put("attachmentName", attachment.getAttachmentName());
                        return map;
                    })
                    .collect(Collectors.toList());
            return new PostResponse(post, attachments);
        } else {
            throw new IllegalArgumentException("Post not found with post_id: " + post_id + ", board_id: " + board_id + ", and club_id: " + club_id);
        }
    }

    @GetMapping("/postdetail/{postId}")
    public PostResponse getPostById(@PathVariable Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            List<Map<String, Object>> attachments = attachmentRepository.findByPostId(post)
                    .stream()
                    .map(attachment -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("attachmentId", attachment.getId());
                        map.put("attachmentName", attachment.getAttachmentName());
                        return map;
                    })
                    .collect(Collectors.toList());
            return new PostResponse(post, attachments);
        } else {
            throw new IllegalArgumentException("Post not found with id: " + postId);
        }
    }

    @Setter
    @Getter
    public class PostResponse {
        // Getters and setters
        private Post post;
        private List<Map<String, Object>> attachmentNames;

        public PostResponse(Post post, List<Map<String, Object>> attachmentNames) {
            this.post = post;
            this.attachmentNames = attachmentNames;
        }

    }
}
