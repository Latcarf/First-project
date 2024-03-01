package com.latcarf.service.comment;

import com.latcarf.model.Comment;
import com.latcarf.model.User;
import com.latcarf.model.reaction.CommentReaction;
import com.latcarf.model.reaction.Reactions;
import com.latcarf.repository.UserRepository;
import com.latcarf.repository.comments.CommentReactionRepository;
import com.latcarf.repository.comments.CommentRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentReactionService {
    private final CommentReactionRepository commentReactionRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public CommentReactionService(CommentReactionRepository commentReactionRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.commentReactionRepository = commentReactionRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public void toggleLike(Long commentId, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new UsernameNotFoundException("Comment not found by: " + commentId));

        Optional<CommentReaction> likeOptional = commentReactionRepository.findByTypeAndUserAndComment(Reactions.LIKE.toString(), user, comment);

        Optional<CommentReaction> dislikeOptional = commentReactionRepository.findByTypeAndUserAndComment(Reactions.DISLIKE.toString(), user, comment);
        dislikeOptional.ifPresent(commentReactionRepository::delete);

        if (likeOptional.isPresent()) {
            commentReactionRepository.delete(likeOptional.get());
        } else {
            commentReactionRepository.save(new CommentReaction(Reactions.LIKE.toString(), user, comment));
        }
    }

    public void toggleDislike(Long commentId, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by: " + username));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new UsernameNotFoundException("Comment not found by: " + commentId));

        Optional<CommentReaction> dislikeOptional = commentReactionRepository.findByTypeAndUserAndComment(Reactions.DISLIKE.toString(), user, comment);

        Optional<CommentReaction> likeOptional = commentReactionRepository.findByTypeAndUserAndComment(Reactions.LIKE.toString(), user, comment);
        likeOptional.ifPresent(commentReactionRepository::delete);

        if (dislikeOptional.isPresent()) {
            commentReactionRepository.delete(dislikeOptional.get());
        } else {
            commentReactionRepository.save(new CommentReaction(Reactions.DISLIKE.toString(), user, comment));
        }
    }

    public Long getLikesCount(Long commentId) {
        return commentReactionRepository.countByTypeAndCommentId(Reactions.LIKE.toString(), commentId);
    }

    public Long getDislikesCount(Long commentId) {
        return commentReactionRepository.countByTypeAndCommentId(Reactions.DISLIKE.toString(), commentId);
    }
}
