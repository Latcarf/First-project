package com.latcarf.service.comment;

import com.latcarf.model.Comment;
import com.latcarf.repository.commentsRepository.CommentRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void createComment(Comment comment, Principal principal) {

    }
}
