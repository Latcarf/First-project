package com.latcarf.controller;

import com.latcarf.model.User;
import com.latcarf.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{id}")
    public String userInfo(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);

        model.addAttribute("user", user);
        model.addAttribute("posts", user.getPosts());

        return "users/user-info";
    }

    @GetMapping("/account")
    public String showMyAccount(Model model, Principal principal) {
        Optional<User> userOpt = userService.getUserByEmail(principal.getName());
        if (userOpt.isPresent()) {
            model.addAttribute("user", userOpt.get());
            model.addAttribute("posts", userOpt.get().getPosts());
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
        if (bindingResult.hasErrors()) {
            return "users/delete-user";
        }
        try {
            userService.validateUserCredentials(user);
            userService.deleteUser(user.getId());
            new SecurityContextLogoutHandler().logout(request, response, null);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            errorValidation(e.getMessage(), bindingResult);
            return "users/delete-user";
        }
    }

    private void errorValidation(String message, BindingResult bindingResult) {
        if ("error.email".equals(message)) {
            bindingResult.rejectValue("email", "error.user", "Wrong email.");
        } else if ("error.password".equals(message)) {
            bindingResult.rejectValue("password", "error.user", "Incorrect password.");
        }
    }
}
