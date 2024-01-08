package com.latcarf.repository.postsRepository;

import com.latcarf.model.Post;
import com.latcarf.model.User;
import com.latcarf.model.likeModels.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByUserAndPost(User user, Post post);
    Long countByPostId(Long postId);
    List<PostLike> findByPost(Post post);

}
