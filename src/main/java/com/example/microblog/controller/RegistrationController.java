package com.example.microblog.controller;

import com.example.microblog.domain.User;
import com.example.microblog.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class RegistrationController {
    @Autowired
    UserService userService;

    @GetMapping("/registration")
    String registration(){
        return "registration";
    }

    @PostMapping("/registration")
    String addUser(User user, Map<String, Object> model){
        if (!userService.addUser(user)) {
            model.put("message", "User exists!");
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivate = userService.activateUser(code);
        if (isActivate) {
            model.addAttribute("success", "User successfully activated");
        } else {
            model.addAttribute("message", "Activation code isnt found!");
        }
        return "login";
    }
}
