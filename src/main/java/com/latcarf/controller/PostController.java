package com.latcarf.controller;

import com.latcarf.model.Post;
import com.latcarf.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Arrays;

@Controller
@RequestMapping("/")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public String listPosts(@RequestParam(required = false) String title,
                            @RequestParam(required = false) String topic,
                            Model model) {
        List<Post> posts = postService.findPosts(title, topic);
        model.addAttribute("posts", posts);
        return "posts/index";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("topics", Arrays.asList("Study", "IT", "Cinema", "Stories", "Games", "Other"));
        return "posts/create";
    }

    @PostMapping
    public String createPost(@ModelAttribute Post post, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        try {
            postService.savePost(post);
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();

            if ("error.title.empty".equals(message)) {
                bindingResult.rejectValue("title", "error.title.empty", "The title of the post cannot be empty.");

            } else if ("error.topic.empty".equals(message)) {
                bindingResult.rejectValue("topic", "error.topic.empty", "The topic of the post cannot be empty.");

            } else if ("error.content.empty".equals(message)) {
                bindingResult.rejectValue("content", "error.content.empty", "The content of the post cannot be empty.");
            } else if ("error.content.length".equals(message)) {
                bindingResult.rejectValue("content", "error.content.length", "The content of the post must contain more than one word.");
            }

            model.addAttribute("topics", Arrays.asList("Study", "IT", "Cinema", "Stories", "Games", "Other"));
            return "posts/create";
        }
        return "redirect:/";
    }


    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "posts/view";
    }
}
