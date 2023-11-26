package com.ajoubooking.demo.controller;

import com.ajoubooking.demo.dto.admin.AdminDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController {

    @GetMapping("/login")
    public String loginPage(@ModelAttribute AdminDto dto, Model model) {
        model.addAttribute("dto", AdminDto.builder().build());  // 빈 객체를 넣어서 th:object에 전달해줘야 함
        return "loginPage";
    }

    @PostMapping("login")
    public String validation(@ModelAttribute AdminDto dto) {  // 객체에 맞게 요청 파라미터 값들을 자동으로 바인딩

        return "";
    }
}
