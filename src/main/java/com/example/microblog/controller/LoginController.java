package com.example.microblog.controller;

import com.example.microblog.domain.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
//@SessionAttributes({"currentUser"})
public class LoginController {
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/loginFailed")
    public String loginError(Model model){
        model.addAttribute("error", "true");
        return "login";
    }

    @PostMapping("/postLogin")
    public String main(){
        return "redirect:/main";
    }

}
