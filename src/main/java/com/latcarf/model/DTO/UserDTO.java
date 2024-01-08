package com.latcarf.model.DTO;

import com.latcarf.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String gender;
    private Integer subscribers;
    private Integer subscriptions;
    private LocalDate createdDate;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.gender = user.getGender();
        this.subscribers = getSubscribers();
        this.subscriptions = getSubscriptions();
        this.createdDate = user.getCreatedDate();
    }
}