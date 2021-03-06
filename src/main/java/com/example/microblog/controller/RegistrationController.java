package com.example.microblog.controller;

import com.example.microblog.domain.User;
import com.example.microblog.domain.dto.CaptchaResponseDto;
import com.example.microblog.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {
    private static final String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    @Autowired
    UserService userService;

    @Autowired
    RestTemplate restTemplate;

    @Value("${recaptcha.secret}")
    String secret;

    @GetMapping("/registration")
    String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    String addUser(@RequestParam("password2") String passwordConfirm,
                   @RequestParam("g-recaptcha-response") String captchaResponse,
                   @Valid User user,
                   BindingResult bindingResult,
                   Model model,
                   RedirectAttributes redirectAttributes) {

        String url = String.format(CAPTCHA_URL, secret, captchaResponse);
        CaptchaResponseDto response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);

        if (!response.isSuccess()) {
            model.addAttribute("messageType", "danger");
            model.addAttribute("captchaError", "Fill captcha");
        }

        boolean isConfirmEmpty = passwordConfirm.isEmpty();
        if (isConfirmEmpty) {
            model.addAttribute("password2Error", "Password confirmation cannot be empty");
        }

        if (user.getPassword() != null && !user.getPassword().equals(passwordConfirm)) {
            model.addAttribute("passwordError", "Passwords are different!");
        }

        if (isConfirmEmpty || bindingResult.hasErrors() || !response.isSuccess()) {

            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            return "registration";
        }

        if (!userService.addUser(user)) {
            model.addAttribute("usernameError", "User exists!");
            return "registration";
        }
        redirectAttributes.addFlashAttribute("message", "Verify your email, please.");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/login";
    }

    @GetMapping("activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivate = userService.activateUser(code);
        if (isActivate) {
            model.addAttribute("messageType", "success");
            model.addAttribute("message", "User successfully activated");
        } else {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Activation code isn't found!");
        }
        return "login";
    }

}
