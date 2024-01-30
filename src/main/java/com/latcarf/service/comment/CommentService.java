package com.latcarf.service.comment;

import com.latcarf.convert.CommentConvert;
import com.latcarf.dto.CommentDTO;
import com.latcarf.model.Comment;
import com.latcarf.model.Post;
import com.latcarf.model.User;
import com.latcarf.repository.UserRepository;
import com.latcarf.repository.commentsRepository.CommentRepository;
import com.latcarf.repository.postsRepository.PostRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

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


    public Comment createComment(Comment comment, Principal principal, Long postId) {
        comment.setUser(getUserByPrincipal(principal));
        comment.setPost(getPostById(postId));
        return commentRepository.save(comment);
    }

    public List<CommentDTO> getCommentsDto(Long id) {
        List<Comment> comments = commentRepository.findCommentByPostId(id);
        return comments.stream().map(commentConvert::convertToCommentDTO).toList();
    }

    public CommentDTO getCommentDto(Comment comment) {
        return commentConvert.convertToCommentDTO(comment);
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
}
