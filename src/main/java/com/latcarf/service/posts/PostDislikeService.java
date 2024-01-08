package com.latcarf.service.posts;

import com.latcarf.model.Post;
import com.latcarf.model.User;
import com.latcarf.model.likeModels.PostDislike;
import com.latcarf.model.likeModels.PostLike;
import com.latcarf.repository.postsRepository.PostLikeRepository;
import com.latcarf.repository.postsRepository.PostRepository;
import com.latcarf.repository.UserRepository;
import com.latcarf.repository.postsRepository.PostDislikeRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostDislikeService {
    private final PostDislikeRepository postDislikeRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public PostDislikeService(PostDislikeRepository postDislikeRepository, PostLikeRepository postLikeRepository, UserRepository userRepository, PostRepository postRepository) {
        this.postDislikeRepository = postDislikeRepository;
        this.postLikeRepository = postLikeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }


    public void toggleDislike(Long postId, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new UsernameNotFoundException("Post not found by: " + postId));

        Optional<PostDislike> dislikeOptional = postDislikeRepository.findByUserAndPost(user, post);

        Optional<PostLike> likeOptional = postLikeRepository.findByUserAndPost(user, post);
        likeOptional.ifPresent(postLikeRepository::delete);

        if (dislikeOptional.isPresent()) {
            postDislikeRepository.delete(dislikeOptional.get());
        } else {
            postDislikeRepository.save(new PostDislike(user, post));
        }
    }

    public Long getDislikesCount(Long postId) {
        return postDislikeRepository.countByPostId(postId);
    }
}
