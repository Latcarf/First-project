package com.latcarf.repository.commentsRepository;

import com.latcarf.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
