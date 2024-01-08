package com.latcarf.controller.postsController;

import com.latcarf.service.posts.PostDislikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class PostDislikeController {
    private final PostDislikeService postDislikeService;

    public PostDislikeController(PostDislikeService postDislikeService) {
        this.postDislikeService = postDislikeService;
    }

    @PostMapping("/dislikes/post/{postId}")
    public ResponseEntity<?> togglePostLike(@PathVariable Long postId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You need to log in to like posts");
        }
        postDislikeService.toggleDislike(postId, principal.getName());

        Long dislikesCount = postDislikeService.getDislikesCount(postId);
        return ResponseEntity.ok(dislikesCount);
    }

    @GetMapping("/dislikes/count/{postId}")
    public ResponseEntity<Long> getDislikesCount(@PathVariable Long postId) {
        Long dislikesCount = postDislikeService.getDislikesCount(postId);
        return ResponseEntity.ok(dislikesCount);
    }
}
