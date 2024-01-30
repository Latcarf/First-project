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
    private final UserConvert userConvert;

    public PostConvert(PostReactionService postLikeService, UserConvert userConvert) {
        this.postReactionService = postLikeService;
        this.userConvert = userConvert;
    }

    public PostDTO convertToPostDTO(Post post) {
        UserDTO userDTO = userConvert.convertToUserDTO(post.getUser());
        Long likes = postReactionService.getLikesCount(post.getId());
        Long dislikes = postReactionService.getDislikesCount(post.getId());

        return new PostDTO(post, userDTO, likes, dislikes);
    }

}
