package com.example.project_c0824m1_jv103.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {
    @GetMapping("")
    public String showHome(){
        return "homePage/homePage";
    }
}
