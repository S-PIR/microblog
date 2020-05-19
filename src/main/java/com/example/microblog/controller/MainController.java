package com.example.microblog.controller;

import com.example.microblog.domain.Message;
import com.example.microblog.domain.User;
import com.example.microblog.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Controller
public class MainController {
    private final MessageRepo messageRepo;

    public MainController(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }


    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/")
    String greeting(Map<String, Object> model){
        return "greeting";
    }

    @GetMapping("/main")
    String main(Map<String, Object> model){
        Iterable<Message> messages = messageRepo.findAll();
        model.put("messages", messages);
        return "main";
    }

    @PostMapping("/main")
    String add(@AuthenticationPrincipal User user,
               @RequestParam String text,
               @RequestParam String tag, Map<String, Object> model,
               @RequestParam("file")MultipartFile file) throws IOException {
        Message message = new Message(text, tag, user);
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String resultFileName = file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFileName));
            message.setFilename(resultFileName);
        }
        if (!text.isEmpty() || !tag.isEmpty() || message.getFilename()!=null) {
            messageRepo.save(message);
        }
        Iterable<Message> messages = messageRepo.findAll();
        model.put("messages", messages);
        return "redirect:/main";
    }

    @GetMapping("/filter")
    String filter(@RequestParam String filter, Map<String, Object> model){
        Iterable<Message> messages;
        if (filter != null && !filter.isEmpty()){
            messages = messageRepo.findByTag(filter);
        } else {
            messages = messageRepo.findAll();
        }
        model.put("messages", messages);
        return "/main";
    }

}
