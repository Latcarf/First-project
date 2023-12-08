package com.latcarf.repository;

import com.latcarf.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTitleContaining(String title);
    List<Post> findByTopicContaining(String topic);
    List<Post> findByTitleContainingAndTopicContaining(String title, String topic);

}
