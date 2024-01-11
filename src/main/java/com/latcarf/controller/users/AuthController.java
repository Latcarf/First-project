package com.latcarf.controller.users;

import com.latcarf.model.User;
import com.latcarf.service.users.UserAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
@Slf4j
public class AuthController {
    private final List<String> gendersList = Arrays.asList("Man", "Woman", "Transgender", "Non-binary",
            "Vecna", "Genderfluid", "Agender",
            "Demogorgon", "Two-Spirit", "Sandwich");
    private final UserAuthenticationService userAuthenticationService;

    public AuthController(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (StringUtils.isNotBlank(error)) {
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
        log.info("Attempting to register new user: {}", user.getEmail());

        if (bindingResult.hasErrors()) {
            return "authentication/register";
        }
        try {
            userAuthenticationService.createUser(user);
            log.info("User registered successfully: {}", user.getEmail());
        } catch (IllegalArgumentException e) {
            log.error("Error registering user: {}", e.getMessage());

            errorValidation(e.getMessage(), bindingResult);

            model.addAttribute("gender", gendersList);
            return "authentication/register";
        }
        return "redirect:/login";
    }

    private void errorValidation(String message, BindingResult bindingResult) {
        log.warn("Validation error: {}", message);

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

