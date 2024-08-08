package com.ac.su.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public CommentCreateResponseDTO createComment(@PathVariable Long postId, @RequestBody CommentCreateRequestDTO commentCreateRequestDTO) {
        return commentService.createComment(postId, commentCreateRequestDTO);
    }

    @GetMapping("/posts/{postId}/comments")
    public List<CommentResponseDTO> getCommentsByPostId(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @DeleteMapping("/posts/{postId}/{commentId}")
    public CommentDeleteResponseDTO deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        return commentService.deleteComment(postId, commentId);
    }

    @PutMapping("/posts/{postId}/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody CommentUpdateDto commentUpdateDto) {
        boolean isUpdated = commentService.updateComment(postId, commentId, commentUpdateDto);
        if (isUpdated) {
            return ResponseEntity.ok("{\"message\":\"성공\"}");
        } else {
            return ResponseEntity.status(400).body("{\"message\":\"실패\"}");
        }
    }
}