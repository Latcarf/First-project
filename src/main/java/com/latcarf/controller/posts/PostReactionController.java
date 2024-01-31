package com.latcarf.controller.posts;

import com.latcarf.service.posts.PostReactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Objects;

@Controller
public class PostReactionController {
    private final PostReactionService postReactionService;

    public PostReactionController(PostReactionService postReactionService) {
        this.postReactionService = postReactionService;
    }

    @PostMapping("/likes/post/{postId}")
    public ResponseEntity<?> togglePostLike(@PathVariable Long postId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You need to log in to like posts");
        }
        postReactionService.toggleLike(postId, principal.getName());

        Long likesCount = postReactionService.getLikesCount(postId);
        return ResponseEntity.ok(likesCount);
    }

    @GetMapping("/likes/count/{postId}")
    public ResponseEntity<Long> getLikesCount(@PathVariable Long postId) {
        Long likesCount = postReactionService.getLikesCount(postId);
        return ResponseEntity.ok(likesCount);
    }
    @GetMapping("view-likes/post/{postId}")
    public String viewLikesPost(@PathVariable Long postId, Model model, Principal principal) {
        model.addAttribute("allUserLikes", postReactionService.getAllUserLikes(postId));
        model.addAttribute("currentUserName", Objects.nonNull(principal) ? principal.getName() : null);

        return "posts/post-likes";
    }

    @PostMapping("/dislikes/post/{postId}")
    public ResponseEntity<?> togglePostDislike(@PathVariable Long postId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You need to log in to like posts");
        }
        postReactionService.toggleDislike(postId, principal.getName());

        Long dislikesCount = postReactionService.getDislikesCount(postId);
        return ResponseEntity.ok(dislikesCount);
    }

    @GetMapping("/dislikes/count/{postId}")
    public ResponseEntity<Long> getDislikesCount(@PathVariable Long postId) {
        Long dislikesCount = postReactionService.getDislikesCount(postId);
        return ResponseEntity.ok(dislikesCount);
    }
}