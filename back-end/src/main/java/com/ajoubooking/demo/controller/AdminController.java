package com.ajoubooking.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController {

    @GetMapping("/login")
    public String loginPage() {
        return "loginPage";
    }

    @PostMapping("login")
    public String validation() {
        return "";
    }
}
