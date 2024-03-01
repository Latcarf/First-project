package com.latcarf.repository.posts;

import com.latcarf.model.Post;
import com.latcarf.model.User;
import com.latcarf.model.reaction.PostReaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {
    Optional<PostReaction> findByTypeAndUserAndPost(String type, User user, Post post);
    Long countByTypeAndPostId(String type, Long postId);
    List<PostReaction> findByTypeAndPostId(String type, Long postId);
}
