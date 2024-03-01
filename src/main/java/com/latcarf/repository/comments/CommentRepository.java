package com.latcarf.repository.comments;

import com.latcarf.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findCommentByPostId(Long id);
    Comment findParentCommentById(Long commentId);
    List<Comment> findReplyCommentByParentCommentId(Long parentCommentId);
}
