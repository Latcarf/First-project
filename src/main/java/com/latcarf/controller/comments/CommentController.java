package com.latcarf.controller.comments;

import com.latcarf.dto.CommentDTO;
import com.latcarf.model.Comment;
import com.latcarf.service.comment.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/create-comment/{postId}")
    @ResponseBody
    public CommentDTO createComment(@PathVariable Long postId, @RequestBody Comment comment, Principal principal) {
        Comment savedComment = commentService.createComment(comment, principal, postId);
        return commentService.getCommentDto(savedComment);
    }

    @GetMapping("/comments/{postId}")
    public String allComments(@PathVariable Long postId, Model model, Principal principal) {
        List<CommentDTO> allComments = commentService.getCommentsDto(postId);

        model.addAttribute("allComments", allComments);
        model.addAttribute("currentUserName", Objects.nonNull(principal) ? principal.getName() : null);
        return "comments/commentsFragment";
    }
}
