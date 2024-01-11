package com.latcarf.convert;

import com.latcarf.dto.PostDTO;
import com.latcarf.dto.UserDTO;
import com.latcarf.model.Post;
import com.latcarf.model.User;
import com.latcarf.service.posts.PostReactionService;
import org.springframework.stereotype.Component;

@Component
public class PostConvert {
    private final PostReactionService postReactionService;

    public PostConvert(PostReactionService postLikeService) {
        this.postReactionService = postLikeService;
    }

    public PostDTO convertToPostDTO(Post post) {
        UserDTO userDTO = convertToUserDTO(post.getUser());
        Long likes = postReactionService.getLikesCount(post.getId());
        Long dislikes = postReactionService.getDislikesCount(post.getId());

        return new PostDTO(post, userDTO, likes, dislikes);
    }

    public UserDTO convertToUserDTO(User user) {
        return new UserDTO(user);
    }
}
