package com.latcarf.model.reaction;

import com.latcarf.model.Post;
import com.latcarf.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "postReactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public PostReaction(String type, User user, Post post) {
        this.type = type;
        this.user = user;
        this.post = post;
    }
}
