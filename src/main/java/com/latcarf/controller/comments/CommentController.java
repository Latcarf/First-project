package com.latcarf.controller.comments;

import com.latcarf.dto.CommentDTO;
import com.latcarf.model.Comment;
import com.latcarf.service.comment.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@Controller
@Slf4j
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/create-comment/{postId}")
    public String createComment(@PathVariable Long postId, @ModelAttribute Comment comment, Principal principal, RedirectAttributes redirectAttributes) {
        commentService.createComment(comment, principal, postId);
        redirectAttributes.addAttribute("postId", postId);

        return "redirect:/posts/" + postId;
    }

    @GetMapping("/comments/{postId}")
    public String allComments(@PathVariable Long postId, Model model, Principal principal) {
        List<CommentDTO> allComments = commentService.getCommentsDto(postId);
        model.addAttribute("allComments", allComments);
        model.addAttribute("currentUserName", Objects.nonNull(principal) ? principal.getName() : null);

        return "fragments/comments-fragment";
    }

    @PostMapping("/delete/comment/{commentId}")
    public String deleteComment(@PathVariable Long commentId, @RequestParam("postId") Long postId){
        commentService.deleteComment(commentId);

        return "redirect:/posts/" + postId;
    }
}
