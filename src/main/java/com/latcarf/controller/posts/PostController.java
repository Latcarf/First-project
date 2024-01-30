package com.latcarf.controller.posts;

import com.latcarf.dto.CommentDTO;
import com.latcarf.dto.PostDTO;
import com.latcarf.model.Comment;
import com.latcarf.model.Post;
import com.latcarf.model.User;
import com.latcarf.service.comment.CommentService;
import com.latcarf.service.posts.PostService;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Objects;

@Controller
@RequestMapping
@Slf4j
public class PostController {

    private final List<String> topicsList = Arrays.asList("Question", "IT", "Study", "Sports", "Since", "History", "Cinema", "Stories", "Games", "Other");
    private final PostService postService;
    private final CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }


    @GetMapping("/")
    public String mainPosts(@RequestParam(required = false) String title,
                            @RequestParam(required = false) String userName,
                            @RequestParam(required = false) String topic,
                            @RequestParam(required = false) String orderByDate,
                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                            @RequestParam(required = false) String sortByLikesOrDislikes,
                            Model model, Principal principal) {


        model.addAttribute("currentUserEmail", Objects.nonNull(principal) ? principal.getName() : null);
        configurePostFiltering(title, userName, topic, orderByDate, startDate, endDate, sortByLikesOrDislikes, model);

        return "posts/index";
    }

    @GetMapping("/filter/userInfo/{userId}")
    public String filterUserPosts(@PathVariable Long userId,
                                  @RequestParam(required = false) String title,
                                  @RequestParam(required = false) String topic,
                                  @RequestParam(required = false) String orderByDate,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                  @RequestParam(required = false) String sortByLikesOrDislikes,
                                  Model model) {

        User user = postService.getUserById(userId);

        model.addAttribute("user", user);
        configurePostFiltering(title, user.getName(), topic, orderByDate, startDate, endDate, sortByLikesOrDislikes, model);

        return "users/user-info";
    }

    @GetMapping("/filter/account/{userId}")
    public String filterMyAccountPosts(@PathVariable Long userId,
                                       @RequestParam(required = false) String title,
                                       @RequestParam(required = false) String topic,
                                       @RequestParam(required = false) String orderByDate,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                       @RequestParam(required = false) String sortByLikesOrDislikes,
                                       Model model) {

        User user = postService.getUserById(userId);

        model.addAttribute("user", user);
        configurePostFiltering(title, user.getName(), topic, orderByDate, startDate, endDate, sortByLikesOrDislikes, model);

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
        log.info("Attempting to create post by user: {}", principal.getName());
        try {
            postService.createPost(post, principal);
        } catch (IllegalArgumentException e) {
            errorValidation(e.getMessage(), bindingResult);

            model.addAttribute("topics", topicsList);
            return "posts/create";
        }
        return "redirect:/";
    }

    @GetMapping("/posts/{postId}")
    public String viewPost(@PathVariable Long postId, @ModelAttribute Comment comment,  Model model, Principal principal) {
        PostDTO postDto = postService.getPostDtoById(postId);
        boolean isOwnerPost = Objects.nonNull(principal) && postService.isOwnerPost(postId, principal.getName());

        model.addAttribute("post", postDto);
        model.addAttribute("isOwnerPost", isOwnerPost);

        List<CommentDTO> allComments = commentService.getCommentsDto(postId);
        model.addAttribute("allComments", allComments);
        model.addAttribute("currentUserName", Objects.nonNull(principal) ? principal.getName() : null);

        return "posts/view";
    }

    @GetMapping("edit/{postId}")
    public String showEditForm(@PathVariable Long postId, Model model, Principal principal) {
        PostDTO postDto = postService.getPostDtoById(postId);

        if (postService.isOwnerPost(postId, principal.getName())) {
            model.addAttribute("post", postDto);
            model.addAttribute("topics", topicsList);
            return "posts/edit";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/update/{postId}")
    public String updatePost(@PathVariable Long postId, @ModelAttribute Post post, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "authentication/register";
        }
        try {
            postService.updatePost(postId, post);
        } catch (IllegalArgumentException e) {
            errorValidation(e.getMessage(), bindingResult);

            model.addAttribute("post", post);
            model.addAttribute("topics", topicsList);
            return "posts/edit";
        }
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/delete/post/{postId}")
    public String deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return "redirect:/";
    }

    private void configurePostFiltering(String title, String userName, String topic, String orderByDate, LocalDate startDate, LocalDate endDate,String sortByLikesOrDislikes, Model model) {
        LocalDateTime startDateTime = Objects.nonNull(startDate) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = Objects.nonNull(endDate) ? endDate.atTime(23, 59, 59) : null;

        List<PostDTO> postDto = postService.searchPosts(title, userName, topic, orderByDate, startDateTime, endDateTime, sortByLikesOrDislikes);

        model.addAttribute("posts", postDto);
        model.addAttribute("topics", topicsList);

        model.addAttribute("searchTitle", title);
        model.addAttribute("searchTopic", topic);
        model.addAttribute("searchStartDate", startDate);
        model.addAttribute("searchEndDate", endDate);
        model.addAttribute("orderByDate", orderByDate);
        model.addAttribute("sortByLikesOrDislikes", sortByLikesOrDislikes);
    }

    private void errorValidation(String message, BindingResult bindingResult) {
        log.warn("Validation error: {}", message);

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
