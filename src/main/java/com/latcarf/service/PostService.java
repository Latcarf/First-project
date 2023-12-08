package com.latcarf.service;

import com.latcarf.repository.PostRepository;
import com.latcarf.model.Post;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    public void savePost(Post post) {
        validatePost(post);
        postRepository.save(post);
    }

    private void validatePost(Post post) {
        if (post.getTitle() == null || post.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("error.title.empty");

        } else if (post.getTopic() == null) {
            throw new IllegalArgumentException("error.topic.empty");

        } else if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("error.content.empty");
        }

        String[] words = post.getContent().split("\\s+");
        if (words.length < 2) {
            throw new IllegalArgumentException("error.content.length");
        }


    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public List<Post> findPosts(String title, String topic) {
        if (title != null && !title.isEmpty() && topic != null && !topic.isEmpty()) {
            return postRepository.findByTitleContainingAndTopicContaining(title, topic);

        } else if (title != null && !title.isEmpty()) {
            return postRepository.findByTitleContaining(title);

        } else if (topic != null && !topic.isEmpty()) {
            return postRepository.findByTopicContaining(topic);

        } else {
            return postRepository.findAll();
        }
    }

}
