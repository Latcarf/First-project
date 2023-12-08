package com.latcarf.service;

import com.latcarf.model.User;
import com.latcarf.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(String name, String gender, String email, String rawPassword) {
        validateUser(name, gender, email, rawPassword);

        String hashedPassword = passwordEncoder.encode(rawPassword);
        User newUser = new User();
        newUser.setName(name);
        newUser.setGender(gender);
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword);

        userRepository.save(newUser);
    }


    private void validateUser(String name, String gender, String email, String password) {

        if (name == null || name.length() < 3) {
            throw new IllegalArgumentException("error.name");
        } else if (name.contains(" ")) {
            throw new IllegalArgumentException("error.name.space");
        }

        if (gender == null) {
            throw new IllegalArgumentException("error.gender.empty");
        }

        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("error.email");
        } else if (email.contains(" ")) {
            throw new IllegalArgumentException("error.email.space");
        }

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("error.password.length");
        } else if (password.contains(" ")) {
            throw new IllegalArgumentException("error.password.space");
        }
    }


}

