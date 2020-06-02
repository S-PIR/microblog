package com.example.microblog.controller;

import com.example.microblog.domain.Message;
import com.example.microblog.domain.User;
import com.example.microblog.domain.dto.MessageDto;
import com.example.microblog.repos.MessageRepo;
import com.example.microblog.repos.UserRepo;
import com.example.microblog.service.user.UserService;
import com.example.microblog.service.user.message.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.IntStream;

@Controller
public class MessageController {
    private static final Integer[] PAGE_SIZES = {5, 10, 25, 50, 100};
    private static final User EMPTY_USER = new User();

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    UserService userService;

    @Autowired
    MessageRepo messageRepo;

    @Autowired
    MessageService messageService;

    @GetMapping("/")
    String greeting(Map<String, Object> model){
        return "greeting";
    }

    @GetMapping("/main")
    String main(@RequestParam(required = false, defaultValue = "") String filter,
                Map<String, Object> model,
                @PageableDefault(sort = {"date"}, direction = Sort.Direction.DESC)Pageable pageable,
                @AuthenticationPrincipal User currentUser){

        Page<MessageDto> page = messageService.messageList(filter, pageable, currentUser);
        model.put("pages", IntStream.range(0, page.getTotalPages()).toArray());
        model.put("messages", page);
        model.put("filter", filter);
        model.put("pageSizes", PAGE_SIZES);
        model.put("url", "/main");
        model.put("userChannel", EMPTY_USER);
        return "main";
    }


    @PostMapping("/main")
    String add(@AuthenticationPrincipal User user,
               @Valid Message message,
               BindingResult bindingResult,
               Model model,
               @RequestParam("file")MultipartFile file,
               @PageableDefault(sort = {"date"}, direction = Sort.Direction.DESC)Pageable pageable) throws IOException {

        message.setAuthor(user);
        message.setDate(LocalDateTime.now());
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("message", message);
        } else {
            saveFile(message, file);
            model.addAttribute("message", null);
            messageRepo.save(message);
        }

        Page<Message> page = messageRepo.findAll(pageable);
        model.addAttribute("messages", page);
        model.addAttribute("pages", IntStream.range(0, page.getTotalPages()).toArray());
        model.addAttribute("url", "/main");
        model.addAttribute("userChannel", EMPTY_USER);
        return "main";
    }

    private void saveFile(@Valid Message message, @RequestParam("file") MultipartFile file) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String resultFileName = file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFileName));
            message.setFilename(resultFileName);
        }
    }

    @GetMapping("/user-messages/{author}")
    public String userMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User author,
            @RequestParam(required = false) Message message,
            @PageableDefault(sort = {"date"}, direction = Sort.Direction.DESC)Pageable pageable,
            Model model) {

//        Set<Message> messages = user.getMessages();
        Page<MessageDto> page = messageService.messageListForUser(author, pageable, currentUser);
        model.addAttribute("pages", IntStream.range(0, page.getTotalPages()).toArray());
        model.addAttribute("pageSizes", PAGE_SIZES);
        System.out.println(IntStream.range(0, page.getTotalPages()).toArray().length);
        model.addAttribute("userChannel", author);
        model.addAttribute("subscriptionsCount", author.getSubscriptions().size());
        model.addAttribute("subscribersCount", author.getSubscribers().size());
        model.addAttribute("isSubscriber", author.getSubscribers().contains(currentUser));
        model.addAttribute("messages", page);
        model.addAttribute("message", message);
        model.addAttribute("isCurrentUser", currentUser.equals(author));
        model.addAttribute("url", "/user-messages/{userId}");
        return "userMessages";
    }

    @PostMapping("/user-messages/{userId}")
    public String updateMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long userId,
            @RequestParam Message message,
            @RequestParam("text") String text,
            @RequestParam("tag") String tag,
            @RequestParam("file") MultipartFile file,
            Model model) throws IOException {

        if (message.getAuthor().equals(currentUser)) {
            if (!StringUtils.isEmpty(text)) {
                message.setText(text);
            }
            if (!StringUtils.isEmpty(tag)) {
                message.setText(tag);
            }
            saveFile(message, file);
        }
        return "redirect:/user-messages/" + userId;
    }

}
