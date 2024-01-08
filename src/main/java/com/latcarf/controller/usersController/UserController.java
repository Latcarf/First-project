package com.latcarf.controller.usersController;

import com.latcarf.model.DTO.UserDTO;
import com.latcarf.model.User;
import com.latcarf.service.users.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
public class UserController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{id}")
    public String userInfo(@PathVariable Long id, Model model) {
        UserDTO userDto = userService.getUserDtoById(id);

        model.addAttribute("user", userDto);
        model.addAttribute("posts", userService.getPostDtoByUserId(id));

        return "users/user-info";
    }

    @GetMapping("/account")
    public String showMyAccount(Model model, Principal principal) {
        Optional<UserDTO> userDtoOpt = userService.getUserDtoByEmail(principal.getName());
        if (userDtoOpt.isPresent()) {
            model.addAttribute("user", userDtoOpt.get());
            model.addAttribute("posts", userService.getPostDtoByUserId(userDtoOpt.get().getId()));
        } else {
            model.addAttribute("userNotFound", true);
        }
        return "users/user-account";
    }

    @GetMapping("/delete/user")
    public String showDeleteAccount(Model model, Principal principal) {
        Optional<User> userOpt = userService.getUserByEmail(principal.getName());
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
        } else {
            model.addAttribute("userNotFound", true);
        }
        return "users/delete-user";
    }

    @PostMapping("/delete/user")
    public String deleteAccount(@ModelAttribute("user") User user, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) {
        logger.info("Attempting to delete user with email: {}", user.getEmail());

        if (bindingResult.hasErrors()) {
            return "users/delete-user";
        }
        try {
            userService.validateUserCredentials(user);
            userService.deleteUser(user.getId());
            new SecurityContextLogoutHandler().logout(request, response, null);

            logger.info("User deleted successfully: {}", user.getEmail());
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting user: {}", e.getMessage());

            errorValidation(e.getMessage(), bindingResult);
            return "users/delete-user";
        }
    }

    private void errorValidation(String message, BindingResult bindingResult) {
        logger.warn("Validation error: {}", message);

        if ("error.email".equals(message)) {
            bindingResult.rejectValue("email", "error.user", "Wrong email.");
        } else if ("error.password".equals(message)) {
            bindingResult.rejectValue("password", "error.user", "Incorrect password.");
        }
    }
}

