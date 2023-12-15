package com.latcarf.controller;

import com.latcarf.model.User;
import com.latcarf.service.UserAuthenticationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class AuthController {

    final private List<String> gendersList = Arrays.asList("Man", "Woman", "Transgender", "Non-binary",
            "Vecna", "Genderfluid", "Agender",
            "Demogorgon", "Two-Spirit", "Sandwich");
    final private UserAuthenticationService userAuthenticationService;

    public AuthController(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", "Invalid email or password.");
        }
        return "authentication/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("gender", gendersList);
        return "authentication/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "authentication/register";
        }
        try {
            userAuthenticationService.createUser(user);
        } catch (IllegalArgumentException e) {
            errorValidation(e.getMessage(), bindingResult);

            model.addAttribute("gender", gendersList);
            return "authentication/register";
        }
        return "redirect:/login";
    }

    private void errorValidation(String message, BindingResult bindingResult) {
        if ("error.name.busy".equals(message)) {
            bindingResult.rejectValue("name", "error.user", "A user with this name already exists.");
        } else if ("error.name".equals(message)) {
            bindingResult.rejectValue("name", "error.user", "Name must be at least 3 characters long.");
        } else if ("error.name.space".equals(message)) {
            bindingResult.rejectValue("name", "error.user", "Name must not contain spaces.");

        } else if ("error.gender.empty".equals(message)) {
            bindingResult.rejectValue("gender", "error.user", "Gender must not be empty.");

        } else if ("error.email.busy".equals(message)) {
            bindingResult.rejectValue("email", "error.user", "A user with this email already exists.");
        } else if ("error.email".equals(message)) {
            bindingResult.rejectValue("email", "error.user", "Invalid email format.");
        } else if ("error.email.space".equals(message)) {
            bindingResult.rejectValue("email", "error.user", "Email must not contain spaces.");

        } else if ("error.password.length".equals(message)) {
            bindingResult.rejectValue("password", "error.user", "Password must be at least 6 characters long.");
        } else if ("error.password.space".equals(message)) {
            bindingResult.rejectValue("password", "error.user", "Password must not contain spaces.");
        }
    }
}

