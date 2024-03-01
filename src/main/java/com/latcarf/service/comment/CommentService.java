package com.latcarf.service.comment;

import com.latcarf.convert.CommentConvert;
import com.latcarf.dto.CommentDTO;
import com.latcarf.model.Comment;
import com.latcarf.model.Post;
import com.latcarf.model.User;
import com.latcarf.repository.UserRepository;
import com.latcarf.repository.comments.CommentRepository;
import com.latcarf.repository.posts.PostRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentConvert commentConvert;


    public CommentService(CommentRepository commentRepository, UserRepository userRepository, PostRepository postRepository, CommentConvert commentConvert) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentConvert = commentConvert;
    }


    public void createComment(Comment comment, Principal principal, Long postId) {
        comment.setUser(getUserByPrincipal(principal));
        comment.setPost(getPostById(postId));

        validateComment(comment);

        commentRepository.save(comment);
    }

    public void createReplyComment(Comment replyComment, Principal principal, Long parentCommentId, Long postId) {
        replyComment.setUser(getUserByPrincipal(principal));
        replyComment.setPost(getPostById(postId));
        replyComment.setParentComment(commentRepository.findParentCommentById(parentCommentId));

        validateComment(replyComment);

        commentRepository.save(replyComment);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<CommentDTO> getCommentsDto(Long id) {
        List<Comment> comments = commentRepository.findCommentByPostId(id);
        return comments.stream()
                .filter(comment -> comment.getParentComment() == null)
                .map(commentConvert::convertToCommentDTO)
                .collect(Collectors.toList());
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new UsernameNotFoundException("Post not found with id: " + postId));
    }

    private User getUserByPrincipal(Principal principal) {
        if (Objects.isNull(principal)) {
            throw new UsernameNotFoundException("Principal is not found");
        }
        String email = principal.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private void validateComment(Comment comment) throws IllegalArgumentException {
        if (StringUtils.isBlank(comment.getText())) {
            throw new IllegalArgumentException("error.text.empty");
        } else if (comment.getText().length() > 60) {
            throw new IllegalArgumentException("error.text.length");
        }
    }
}
