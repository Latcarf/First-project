package com.latcarf.controller.postsController;

import com.latcarf.service.posts.PostLikeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class PostLikeController {
    private final PostLikeService postLikeService;

    public PostLikeController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    @PostMapping("/likes/post/{postId}")
    public ResponseEntity<?> togglePostLike(@PathVariable Long postId, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You need to log in to like posts");
        }
        postLikeService.toggleLike(postId, principal.getName());

        Long likesCount = postLikeService.getLikesCount(postId);
        return ResponseEntity.ok(likesCount);
    }

    @GetMapping("/likes/count/{postId}")
    public ResponseEntity<Long> getLikesCount(@PathVariable Long postId) {
        Long likesCount = postLikeService.getLikesCount(postId);
        return ResponseEntity.ok(likesCount);
    }
    @GetMapping("view-likes/post/{postId}")
    public String viewLikesPost(@PathVariable Long postId, Model model) {
        model.addAttribute("allUserLikes", postLikeService.getAllUserLikes(postId));

        return "posts/post-likes";
    }
}