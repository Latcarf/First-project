package com.latcarf.specification;

import com.latcarf.model.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import com.latcarf.model.Post;

import java.time.LocalDateTime;

public class PostSpecifications {

    public static Specification<Post> hasTitle(String title) {
        return (post, cq, cb) -> title == null ? cb.conjunction() : cb.like(post.get("title"), "%" + title + "%");
    }

    public static Specification<Post> hasUserName(String userName) {
        return (post, cq, cb) -> {
            if (userName == null) {
                return cb.conjunction();
            }
            Join<Post, User> userJoin = post.join("user");
            return cb.like(userJoin.get("name"), userName + "%");
        };
    }

    public static Specification<Post> hasTopic(String topic) {
        return (post, cq, cb) -> topic == null ? cb.conjunction() : cb.equal(post.get("topic"), topic);
    }

    public static Specification<Post> isCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (post, cq, cb) -> {
            if (startDate == null || endDate == null) {
                return cb.conjunction();
            }
            return cb.between(post.get("createdDateTime"), startDate, endDate);
        };
    }
}
