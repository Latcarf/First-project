package com.latcarf.service.users;

import com.latcarf.model.User;
import com.latcarf.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserAuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(User user) {
        validateUser(user);

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        userRepository.save(user);

        log.info("Saving new User with email: {}", user.getEmail());
    }

    private void validateUser(User user) {

        if (userRepository.findByName(user.getName()).isPresent()) {
            throw new IllegalArgumentException("error.name.busy");
        } else if (StringUtils.isBlank(user.getName()) || user.getName().length() < 3) {
            throw new IllegalArgumentException("error.name");
        } else if (user.getName().contains(" ")) {
            throw new IllegalArgumentException("error.name.space");
        }

        if (StringUtils.isBlank(user.getGender())) {
            throw new IllegalArgumentException("error.gender.empty");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("error.email.busy");
        } else if (StringUtils.isBlank(user.getEmail()) || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("error.email");
        } else if (user.getEmail().contains(" ")) {
            throw new IllegalArgumentException("error.email.space");
        }

        if (StringUtils.isBlank(user.getPassword()) || user.getPassword().length() < 6) {
            throw new IllegalArgumentException("error.password.length");
        } else if (user.getPassword().contains(" ")) {
            throw new IllegalArgumentException("error.password.space");
        }
    }


}

