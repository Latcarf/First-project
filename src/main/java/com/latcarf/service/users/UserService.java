package com.latcarf.service.users;

import com.latcarf.model.DTO.PostDTO;
import com.latcarf.model.DTO.UserDTO;
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
    private final PasswordEncoder passwordEncoder;
    private final PostService postService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       PostService postService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.postService = postService;
    }


    public UserDTO getUserDtoById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return convertToUserDTO(user);
    }

    public Optional<UserDTO> getUserDtoByEmail(String email) {
        return userRepository.findByEmail(email).map(this::convertToUserDTO);
    }

    public List<PostDTO> getPostDtoByUserId(Long userId) {
        return postService.getPostsByUserId(userId).stream()
                .map(postService::convertToPostDTO)
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

    private UserDTO convertToUserDTO(User user) {
        return new UserDTO(user);
    }
}

