package com.latcarf.controller.comments;

import com.latcarf.service.comment.CommentReactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class CommentReactionController {
    private final CommentReactionService commentReactionService;
    public CommentReactionController(CommentReactionService commentReactionService) {
        this.commentReactionService = commentReactionService;
    }

    @PostMapping("/likes/comment/{commentId}")
    public ResponseEntity<?> togglePostLike(@PathVariable Long commentId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You need to log in to like comment");
        }
        commentReactionService.toggleLike(commentId, principal.getName());

        Long likesCount = commentReactionService.getLikesCount(commentId);
        return ResponseEntity.ok(likesCount);
    }

    @GetMapping("/likes/count/comment/{commentId}")
    public ResponseEntity<Long> getLikesCount(@PathVariable Long commentId) {
        Long likesCount = commentReactionService.getLikesCount(commentId);
        return ResponseEntity.ok(likesCount);
    }

    @PostMapping("/dislikes/comment/{commentId}")
    public ResponseEntity<?> togglePostDislike(@PathVariable Long commentId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You need to log in to dislike comment");
        }
        commentReactionService.toggleDislike(commentId, principal.getName());

        Long dislikesCount = commentReactionService.getDislikesCount(commentId);
        return ResponseEntity.ok(dislikesCount);
    }

    @GetMapping("/dislikes/count/comment/{commentId}")
    public ResponseEntity<Long> getDislikesCount(@PathVariable Long commentId) {
        Long dislikesCount = commentReactionService.getDislikesCount(commentId);
        return ResponseEntity.ok(dislikesCount);
    }
}
