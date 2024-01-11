package com.latcarf.service.users;

import com.latcarf.convert.UserConvert;
import com.latcarf.convert.PostConvert;
import com.latcarf.dto.PostDTO;
import com.latcarf.dto.UserDTO;
import com.latcarf.model.User;
import com.latcarf.repository.UserRepository;
import com.latcarf.service.posts.PostService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PostService postService;
    private final UserConvert userConvert;
    private final PostConvert postConvert;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PostService postService,
                       UserConvert userConvert, PostConvert postConvert,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.postService = postService;
        this.userConvert = userConvert;
        this.postConvert = postConvert;
        this.passwordEncoder = passwordEncoder;
    }


    public UserDTO getUserDtoById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return userConvert.convertToUserDTO(user);
    }

    public Optional<UserDTO> getUserDtoByEmail(String email) {
        return userRepository.findByEmail(email).map(userConvert::convertToUserDTO);
    }

    public List<PostDTO> getPostDtoByUserId(Long userId) {
        return postService.getPostsByUserId(userId).stream()
                .map(postConvert::convertToPostDTO)
                .collect(Collectors.toList());
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    public void validateUserCredentials(User user) {
        Optional<User> existingUserOpt = getUserByEmail(user.getEmail());
        if (existingUserOpt.isEmpty()) {
            throw new IllegalArgumentException("error.email");
        }

        User existingUser = existingUserOpt.get();
        if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
            throw new IllegalArgumentException("error.password");
        }
    }


}

