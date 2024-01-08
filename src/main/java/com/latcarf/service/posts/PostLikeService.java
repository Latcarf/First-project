package com.latcarf.service.posts;

import com.latcarf.model.Post;
import com.latcarf.model.User;
import com.latcarf.model.likeModels.PostDislike;
import com.latcarf.model.likeModels.PostLike;
import com.latcarf.repository.postsRepository.PostDislikeRepository;
import com.latcarf.repository.postsRepository.PostRepository;
import com.latcarf.repository.UserRepository;
import com.latcarf.repository.postsRepository.PostLikeRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostDislikeRepository postDislikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public PostLikeService(PostLikeRepository postLikeRepository, UserRepository userRepository, PostRepository postRepository, PostDislikeRepository postDislikeRepository) {
        this.postLikeRepository = postLikeRepository;
        this.postDislikeRepository = postDislikeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public void toggleLike(Long postId, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new UsernameNotFoundException("Post not found by: " + postId));

        Optional<PostLike> likeOptional = postLikeRepository.findByUserAndPost(user, post);

        Optional<PostDislike> dislikeOptional = postDislikeRepository.findByUserAndPost(user, post);
        dislikeOptional.ifPresent(postDislikeRepository::delete);

        if (likeOptional.isPresent()) {
            postLikeRepository.delete(likeOptional.get());
        } else {
            postLikeRepository.save(new PostLike(user, post));
        }
    }

    public Long getLikesCount(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    public List<User> getAllUserLikes(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new UsernameNotFoundException("Post not found by: " + postId));

        return postLikeRepository.findByPost(post).stream()
                .map(PostLike::getUser)
                .collect(Collectors.toList());
    }


}
