package com.latcarf.dto;

import com.latcarf.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private Long id;
    private String text;
    private String type;
    private LocalDateTime createdCommentDateTime;
    private PostDTO postDTO;
    private UserDTO userDTO;
    private Comment parentComment;
    private List<Comment> replies;
    private Long likesCount;
    private Long dislikesCount;

    public CommentDTO(Comment comment, PostDTO postDTO, UserDTO userDTO, Long likesCount, Long dislikesCount) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.type = comment.getType();
        this.createdCommentDateTime = comment.getCreatedCommentDateTime();
        this.postDTO = postDTO;
        this.userDTO = userDTO;
        this.parentComment = comment.getParentComment();
        this.replies = comment.getReplies();
        this.likesCount = likesCount;
        this.dislikesCount = dislikesCount;
    }
}
