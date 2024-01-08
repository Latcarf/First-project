package com.latcarf.model.DTO;

import com.latcarf.model.Post;
import com.latcarf.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long id;
    private String title;
    private String topic;
    private String content;
    private LocalDateTime createdDateTime;
    private Long likesCount;
    private Long dislikesCount;
    private UserDTO user;

    public PostDTO(Post post, UserDTO userDTO, Long likesCount, Long dislikesCount) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.topic = post.getTopic();
        this.content = post.getContent();
        this.createdDateTime = post.getCreatedDateTime();
        this.likesCount = likesCount;
        this.dislikesCount = dislikesCount;
        this.user = userDTO;
    }
}
