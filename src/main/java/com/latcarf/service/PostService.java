package com.latcarf.service;

import com.latcarf.model.Post;
import com.latcarf.model.User;
import com.latcarf.repository.PostRepository;
import com.latcarf.repository.UserRepository;
import com.latcarf.specification.PostSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }


    public List<Post> searchPosts(String title, String userName, String topic, String orderByDate, LocalDateTime startDate, LocalDateTime endDate) {
        Specification<Post> spec = Specification.where(null);

        if (title != null && !title.isEmpty()) {
            spec = spec.and(PostSpecifications.hasTitle(title));
        }
        if (userName != null && !userName.isEmpty()) {
            spec = spec.and((PostSpecifications.hasUserName(userName)));
        }
        if (topic != null && !topic.isEmpty()) {
            spec = spec.and(PostSpecifications.hasTopic(topic));
        }
        if (startDate != null && endDate != null) {
            spec = spec.and(PostSpecifications.isCreatedBetween(startDate, endDate));
        }

        List<Post> posts = spec == null ? postRepository.findAll() : postRepository.findAll(spec);
        return getSortedPosts(posts, orderByDate);
    }


    public void createPost(Post post, Principal principal) {
        validatePost(post);

        User user = getUserByPrincipal(principal);
        post.setUser(user);

        postRepository.save(post);
    }

    public void updatePost(Long id, Post updatedPost) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Post not found with id: " + id));

        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setTopic(updatedPost.getTopic());

        validatePost(post);

        postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("Post not found by: " + id));
    }
    public boolean isOwner(Long postId, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found post: " + postId));

        return post.getUser().getEmail().equals(userEmail);
    }

    private void validatePost(Post post) {
        if (post.getTitle() == null || post.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("error.title.empty");

        } else if (post.getTopic() == null  ) {
            throw new IllegalArgumentException("error.topic.empty");

        } else if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("error.content.empty");
        }

        String[] words = post.getContent().split("\\s+");
        if (words.length < 2) {
            throw new IllegalArgumentException("error.content.length");
        }
    }

    private List<Post> getSortedPosts(List<Post> posts, String orderByDate) {
        if (orderByDate == null || orderByDate.isEmpty()) {
            orderByDate = "desc";
        }

        if ("asc".equalsIgnoreCase(orderByDate)) {
            posts.sort(Comparator.comparing(Post::getCreatedDateTime));
        } else if ("desc".equalsIgnoreCase(orderByDate)) {
            posts.sort((p1, p2) -> p2.getCreatedDateTime().compareTo(p1.getCreatedDateTime()));
        }
        return posts;
    }

    private User getUserByPrincipal(Principal principal) {
        if (principal == null) {
            throw new UsernameNotFoundException("Principal is not found");
        }
        String email = principal.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }


}
