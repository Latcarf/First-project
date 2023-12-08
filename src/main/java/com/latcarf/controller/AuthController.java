package com.latcarf.controller;

import com.latcarf.model.User;
import com.latcarf.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

@Controller
public class AuthController {

    final private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("authentication/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", "Invalid email or password.");
        }
        return "authentication/login";
    }

    @GetMapping("authentication/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("gender", Arrays.asList("Man", "Woman", "Transgender", "Non-binary",
                "Vecna", "Genderfluid", "Agender",
                "Demogorgon", "Two-Spirit", "Sandwich"));
        return "authentication/register";
    }

    @PostMapping("authentication/register")
    public String registerUser(@ModelAttribute User user, Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "authentication/register";
        }

        try {
            userService.createUser(user.getName(), user.getGender(), user.getEmail(), user.getPassword());
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();

            if ("error.name".equals(message)) {
                bindingResult.rejectValue("name", "error.user", "Name must be at least 3 characters long.");
            } else if ("error.name.space".equals(e.getMessage())) {
                bindingResult.rejectValue("name", "error.user", "Name must not contain spaces.");

            } else if ("error.gender.empty".equals(e.getMessage())) {
                bindingResult.rejectValue("gender", "error.user", "Gender must not be empty.");

            } else if ("error.email".equals(e.getMessage())) {
                bindingResult.rejectValue("email", "error.user", "Invalid email format.");
            } else if ("error.email.space".equals(e.getMessage())) {
                bindingResult.rejectValue("email", "error.user", "Email must not contain spaces.");

            } else if ("error.password.length".equals(e.getMessage())) {
                bindingResult.rejectValue("password", "error.user", "Password must be at least 6 characters long.");
            } else if ("error.password.space".equals(e.getMessage())) {
                bindingResult.rejectValue("password", "error.user", "Password must not contain spaces.");
            }

            model.addAttribute("gender", Arrays.asList("Man", "Woman", "Transgender", "Non-binary",
                    "Vecna", "Genderfluid", "Agender",
                    "Demogorgon", "Two-Spirit", "Sandwich"));

            return "authentication/register";
        }

        return "redirect:/authentication/login";
    }
}

