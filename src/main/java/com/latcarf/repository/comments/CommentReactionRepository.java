package com.latcarf.repository.comments;

import com.latcarf.model.Comment;
import com.latcarf.model.User;
import com.latcarf.model.reaction.CommentReaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {
    Optional<CommentReaction> findByTypeAndUserAndComment(String string, User user, Comment comment);
    Long countByTypeAndCommentId(String string, Long commentId);
}
