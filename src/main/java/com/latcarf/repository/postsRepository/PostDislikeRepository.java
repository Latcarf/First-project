package com.latcarf.repository.postsRepository;

import com.latcarf.model.Post;
import com.latcarf.model.User;
import com.latcarf.model.likeModels.PostDislike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostDislikeRepository extends JpaRepository<PostDislike, Long> {
    Optional<PostDislike> findByUserAndPost(User user, Post post);
    Long countByPostId(Long postId);
}
