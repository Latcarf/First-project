package com.latcarf.service.posts;

import com.latcarf.convert.PostConvert;
import com.latcarf.dto.PostDTO;
import com.latcarf.model.Post;
import com.latcarf.model.User;
import com.latcarf.repository.postsRepository.PostRepository;
import com.latcarf.repository.UserRepository;
import com.latcarf.service.posts.specification.PostSpecifications;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostConvert postConvert;

    public PostService(PostRepository postRepository, UserRepository userRepository, PostConvert postConvert) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postConvert = postConvert;
    }


    public List<PostDTO> searchPosts(String title, String userName, String topic, String orderByDate, LocalDateTime startDate, LocalDateTime endDate, String sortByReaction) {
        Specification<Post> spec = Specification.where(null);

        if (StringUtils.isNotBlank(title)){
            spec = spec.and(PostSpecifications.hasTitle(title));
        }
        if (StringUtils.isNotBlank(userName)) {
            spec = spec.and((PostSpecifications.hasUserName(userName)));
        }
        if (StringUtils.isNotBlank(topic)) {
            spec = spec.and(PostSpecifications.hasTopic(topic));
        }
        if (Objects.nonNull(startDate) && Objects.nonNull(endDate)) {
            spec = spec.and(PostSpecifications.isCreatedBetween(startDate, endDate));
        }

        List<Post> posts = Objects.isNull(spec) ? postRepository.findAll() : postRepository.findAll(spec);
        List<PostDTO> postDTOs = posts.stream().map(postConvert::convertToPostDTO).collect(Collectors.toList());

        if (StringUtils.isNotBlank(sortByReaction)) {
            return getMostReaction(postDTOs, sortByReaction);
        }


        return getSortByRecency(postDTOs, orderByDate);
    }

    public void createPost(Post post, Principal principal) throws IllegalArgumentException{
        User user = getUserByPrincipal(principal);
        post.setUser(user);

        validatePost(post);

        postRepository.save(post);
    }


    public void updatePost(Long id, Post updatedPost) throws IllegalArgumentException{
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
        return postConvert.convertToPostDTO(post);
    }

    public boolean isOwnerPost(Long postId, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found post: " + postId));

        return post.getUser().getEmail().equals(userEmail);
    }

    private List<PostDTO> getMostReaction(List<PostDTO> postDTOs, String sortByReaction) {
        if ("mostLikes".equals(sortByReaction)) {
            postDTOs.sort(Comparator.comparing(PostDTO::getLikesCount).reversed());
        } else if ("mostDislikes".equals(sortByReaction)) {
            postDTOs.sort(Comparator.comparing(PostDTO::getDislikesCount).reversed());
        }
        return postDTOs;
    }

    private List<PostDTO> getSortByRecency(List<PostDTO> postDTOs, String orderByDate) {
        if (StringUtils.isBlank(orderByDate)) {
            orderByDate = "desc";
        }

        if ("asc".equalsIgnoreCase(orderByDate)) {
            postDTOs.sort(Comparator.comparing(PostDTO::getCreatedDateTime));
        } else if ("desc".equalsIgnoreCase(orderByDate)) {
            postDTOs.sort((p1, p2) -> p2.getCreatedDateTime().compareTo(p1.getCreatedDateTime()));
        }

        return postDTOs;
    }

    private void validatePost(Post post) throws IllegalArgumentException {
        if (StringUtils.isBlank(post.getTitle())) {
            throw new IllegalArgumentException("error.title.empty");

        } else if (StringUtils.isBlank(post.getTopic())) {
            throw new IllegalArgumentException("error.topic.empty");

        } else if (StringUtils.isBlank(post.getContent())) {
            throw new IllegalArgumentException("error.content.empty");
        }

        String[] words = post.getContent().split("\\s+");
        if (words.length < 2) {
            throw new IllegalArgumentException("error.content.length");
        }
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
