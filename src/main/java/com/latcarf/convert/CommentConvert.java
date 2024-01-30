package com.latcarf.convert;

import com.latcarf.dto.CommentDTO;
import com.latcarf.dto.PostDTO;
import com.latcarf.dto.UserDTO;
import com.latcarf.model.Comment;
import com.latcarf.service.comment.CommentReactionService;
import org.springframework.stereotype.Component;

@Component
public class CommentConvert {
    private final CommentReactionService commentReactionService;
    private final PostConvert postConvert;
    private final UserConvert userConvert;

    public CommentConvert(CommentReactionService commentReactionService, PostConvert postConvert, UserConvert userConvert) {
        this.commentReactionService = commentReactionService;
        this.postConvert = postConvert;
        this.userConvert = userConvert;
    }

    public CommentDTO convertToCommentDTO(Comment comment) {
        PostDTO postDTO = postConvert.convertToPostDTO(comment.getPost());
        UserDTO userDTO = userConvert.convertToUserDTO(comment.getUser());
        Long likes = commentReactionService.getLikesCount(comment.getId());
        Long dislikes = commentReactionService.getDislikesCount(comment.getId());

        return new CommentDTO(comment, postDTO, userDTO, likes, dislikes);
    }
}
