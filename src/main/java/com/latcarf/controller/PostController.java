package com.latcarf.controller;

import com.latcarf.model.Post;
import com.latcarf.model.User;
import com.latcarf.service.PostService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping
public class PostController {

    private final List<String> topicsList = Arrays.asList("IT", "Study", "Sports", "Since", "History", "Cinema", "Stories", "Games", "Other");
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public String mainPosts(@RequestParam(required = false) String title,
                            @RequestParam(required = false) String userName,
                            @RequestParam(required = false) String topic,
                            @RequestParam(required = false) String orderByDate,
                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                            Model model, Principal principal) {

        model.addAttribute("currentUserEmail", principal != null ? principal.getName() : null);
        configurePostFiltering(title, userName, topic, orderByDate, startDate, endDate, model);

        return "posts/index";
    }

    @GetMapping("/filter/userInfo/{userId}")
    public String filterUserPosts(@PathVariable Long userId,
                                     @RequestParam(required = false) String title,
                                     @RequestParam(required = false) String topic,
                                     @RequestParam(required = false) String orderByDate,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                     Model model) {

        User user = postService.getUserById(userId);

        model.addAttribute("user", user);
        configurePostFiltering(title, user.getName(), topic, orderByDate, startDate, endDate, model);

        return "users/user-info";
    }

    @GetMapping("/filter/account/{userId}")
    public String filterMyAccountPosts(@PathVariable Long userId,
                                          @RequestParam(required = false) String title,
                                          @RequestParam(required = false) String topic,
                                          @RequestParam(required = false) String orderByDate,
                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                          Model model) {

        User user = postService.getUserById(userId);

        model.addAttribute("user", user);
        configurePostFiltering(title, user.getName(), topic, orderByDate, startDate, endDate, model);

        return "users/user-account";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("topics", topicsList);
        return "posts/create";
    }

    @PostMapping("/create")
    public String createPost(@ModelAttribute Post post, BindingResult bindingResult, Model model, Principal principal) {
        try {
            postService.createPost(post, principal);
        } catch (IllegalArgumentException e) {
            errorValidation(e.getMessage(), bindingResult);

            model.addAttribute("topics", topicsList);
            return "posts/create";
        }
        return "redirect:/";
    }

    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id, Model model, Principal principal) {
        Post post = postService.getPostById(id);
        boolean isOwner = principal != null && postService.isOwner(id, principal.getName());

        model.addAttribute("post", post);
        model.addAttribute("isOwner", isOwner);
        return "posts/view";
    }

    @GetMapping("edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        Post post = postService.getPostById(id);
        if (postService.isOwner(id, principal.getName())) {
            model.addAttribute("post", post);
            model.addAttribute("topics", topicsList);
            return "posts/edit";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/update/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post post, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "authentication/register";
        }
        try {
            postService.updatePost(id, post);
        } catch (IllegalArgumentException e) {
            errorValidation(e.getMessage(), bindingResult);

            model.addAttribute("post", post);
            model.addAttribute("topics", topicsList);
            return "posts/edit";
        }
        return "redirect:/posts/" + id;
    }

    @PostMapping("/delete/post/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/";
    }

    private void configurePostFiltering(String title, String userName, String topic, String orderByDate, LocalDate startDate, LocalDate endDate, Model model) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

        List<Post> posts = postService.searchPosts(title, userName, topic, orderByDate, startDateTime, endDateTime);

        model.addAttribute("posts", posts);
        model.addAttribute("topics", topicsList);

        model.addAttribute("searchTitle", title);
        model.addAttribute("searchTopic", topic);
        model.addAttribute("searchStartDate", startDate);
        model.addAttribute("searchEndDate", endDate);
        model.addAttribute("orderByDate", orderByDate);

    }

    private void errorValidation(String message, BindingResult bindingResult) {
        if ("error.title.empty".equals(message)) {
            bindingResult.rejectValue("title", "error.title.empty", "The title of the post cannot be empty.");

        } else if ("error.topic.empty".equals(message)) {
            bindingResult.rejectValue("topic", "error.topic.empty", "The topic of the post cannot be empty.");

        } else if ("error.content.empty".equals(message)) {
            bindingResult.rejectValue("content", "error.content.empty", "The content of the post cannot be empty.");
        } else if ("error.content.length".equals(message)) {
            bindingResult.rejectValue("content", "error.content.length", "The content of the post must contain more than one word.");
        }
    }
}
