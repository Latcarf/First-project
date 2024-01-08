package com.latcarf.service.posts;

import com.latcarf.model.DTO.PostDTO;
import com.latcarf.model.DTO.UserDTO;
import com.latcarf.model.Post;
import com.latcarf.model.User;
import com.latcarf.repository.postsRepository.PostRepository;
import com.latcarf.repository.UserRepository;
import com.latcarf.service.posts.specification.PostSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeService postLikeService;
    private final PostDislikeService postDislikeService;

    public PostService(PostRepository postRepository, UserRepository userRepository, PostLikeService postLikeService, PostDislikeService postDislikeService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postLikeService = postLikeService;
        this.postDislikeService = postDislikeService;
    }

    public void createPost(Post post, Principal principal) {
        validatePost(post);

        User user = getUserByPrincipal(principal);
        post.setUser(user);

        postRepository.save(post);
    }

    public List<PostDTO> searchPosts(String title, String userName, String topic, String orderByDate, LocalDateTime startDate, LocalDateTime endDate, String sortByLikesOrDislikes) {
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
        List<PostDTO> postDTOs = posts.stream().map(this::convertToPostDTO).collect(Collectors.toList());


        if (sortByLikesOrDislikes != null) {
            return getSortMostLikesOrDislikes(postDTOs, sortByLikesOrDislikes);
        }

        return getSortByRecency(postDTOs, orderByDate);
    }

    private List<PostDTO> getSortMostLikesOrDislikes(List<PostDTO> postDTOs, String sortByLikesOrDislikes) {
        if ("mostLikes".equals(sortByLikesOrDislikes)) {
            postDTOs.sort(Comparator.comparing(PostDTO::getLikesCount).reversed());
        } else if ("mostDislikes".equals(sortByLikesOrDislikes)) {
            postDTOs.sort(Comparator.comparing(PostDTO::getDislikesCount).reversed());
        }
        return postDTOs;
    }

    private List<PostDTO> getSortByRecency(List<PostDTO> postDTOs, String orderByDate) {
        if (orderByDate == null || orderByDate.isEmpty()) {
            orderByDate = "desc";
        }

        if ("asc".equalsIgnoreCase(orderByDate)) {
            postDTOs.sort(Comparator.comparing(PostDTO::getCreatedDateTime));
        } else if ("desc".equalsIgnoreCase(orderByDate)) {
            postDTOs.sort((p1, p2) -> p2.getCreatedDateTime().compareTo(p1.getCreatedDateTime()));
        }

        return postDTOs;
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

    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findAllByUserId(userId);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
    }

    public PostDTO getPostDtoById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Post not found with id: " + id));
        return convertToPostDTO(post);
    }

    public boolean isOwner(Long postId, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found post: " + postId));

        return post.getUser().getEmail().equals(userEmail);
    }

    public PostDTO convertToPostDTO(Post post) {
        UserDTO userDTO = convertToUserDTO(post.getUser());
        Long likes = postLikeService.getLikesCount(post.getId());
        Long dislikes = postDislikeService.getDislikesCount(post.getId());

        return new PostDTO(post, userDTO, likes, dislikes);
    }

    private UserDTO convertToUserDTO(User user) {
        return new UserDTO(user);
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

    private User getUserByPrincipal(Principal principal) {
        if (principal == null) {
            throw new UsernameNotFoundException("Principal is not found");
        }
        String email = principal.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

}
