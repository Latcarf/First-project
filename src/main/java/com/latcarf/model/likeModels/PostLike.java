package com.latcarf.model.likeModels;

import com.latcarf.model.Post;
import com.latcarf.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_likes")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class PostLike extends Like {

    @ManyToOne
    @JoinColumn
    private Post post;

    public PostLike(User user, Post post) {
        super.user = user;
        this.post = post;
    }
}