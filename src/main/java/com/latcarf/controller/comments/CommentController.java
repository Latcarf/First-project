package com.latcarf.controller.comments;

import com.latcarf.dto.CommentDTO;
import com.latcarf.model.Comment;
import com.latcarf.model.Post;
import com.latcarf.service.comment.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    public String createComment(@PathVariable Long postId,
                                @ModelAttribute("comment") Comment comment,
                                BindingResult bindingResult,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            commentService.createComment(comment, principal, postId);
        } catch (IllegalArgumentException e) {
            errorValidation(e.getMessage(), bindingResult);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.comment", bindingResult);
        }
        redirectAttributes.addAttribute("postId", postId);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/create-reply-comment/{parentCommentId}/{postId}")
    public String createReplyComment(@PathVariable Long parentCommentId,
                                     @PathVariable Long postId,
                                     @ModelAttribute("replyComment") Comment replyComment,
                                     BindingResult bindingResult,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) {
        try {
            commentService.createReplyComment(replyComment, principal, parentCommentId, postId);
        } catch (IllegalArgumentException e) {
            errorValidation(e.getMessage(), bindingResult);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.comment", bindingResult);
        }

        return "redirect:/posts/" + replyComment.getPost().getId();

    }

    @GetMapping("/comments/{postId}")
    public String allComments(@PathVariable Long postId, Model model, Principal principal) {
        List<CommentDTO> allComments = commentService.getCommentsDto(postId);

        model.addAttribute("comment", new Comment());
        model.addAttribute("allComments", allComments);
        model.addAttribute("replyComment", new Comment());
        model.addAttribute("currentUserName", Objects.nonNull(principal) ? principal.getName() : null);

        return "fragments/comments-fragment";
    }

    @PostMapping("/delete/comment/{commentId}")
    public String deleteComment(@PathVariable Long commentId, @RequestParam("postId") Long postId) {
        commentService.deleteComment(commentId);

        return "redirect:/posts/" + postId;
    }

    private void errorValidation(String message, BindingResult bindingResult) {
        if ("error.text.empty".equals(message)) {
            bindingResult.rejectValue("text", "error.text.empty", "The text of the comment cannot be empty.");
        } else if ("error.text.length".equals(message)) {
            bindingResult.rejectValue("text", "error.text.length", "The comment text must be less than 60 characters.");
        }
    }
}
