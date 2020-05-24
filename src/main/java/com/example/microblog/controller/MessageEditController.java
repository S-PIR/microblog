package com.example.microblog.controller;

import com.example.microblog.domain.Message;
import com.example.microblog.domain.User;
import com.example.microblog.repos.UserRepo;
import com.example.microblog.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Set;

@Controller
public class MessageEditController {
    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private UserRepo userRepo;

    @GetMapping("/user-messages/{username}")
    public String userMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String username,
            @RequestParam(required = false) Message message,
            Model model) {

        User user = userRepo.findByUsername(username);
        Set<Message> messages = user.getMessages();
        model.addAttribute("messages", messages);
        model.addAttribute("message", message);
        model.addAttribute("isCurrentUser", currentUser.equals(user));
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
            if (message != null) {
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
        }
        return "redirect:/user-messages/" + userId;
    }


}
