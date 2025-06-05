package com.example.project_c0824m1_jv103.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/images")
public class ImageController {

    @GetMapping("/upload")
    public String showUploadPage() {
        return "image/upload-images";
    }
} 