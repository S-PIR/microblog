package com.example.microblog;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class GreetingController {

    @GetMapping("/greeting")
    String greeting(@RequestParam(name = "name", required = false,
            defaultValue = "World") String name, Map<String, Object> model){
        model.put("name", name);
        return "greeting";
    }

    @GetMapping
    String main(Map<String, Object> model){
        model.put("some", "Hello, anyone!");
        return "main";
    }


}
