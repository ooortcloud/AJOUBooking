package com.ajoubooking.demo.controller.admin;

import com.ajoubooking.demo.dto.admin.ChangeColumnDto;
import com.ajoubooking.demo.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class AdminColumnController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/column")
    public String changeColumn(Model model) {
        model.addAttribute("columnDto", ChangeColumnDto.builder().build());
        return "/column/changeColumn";
    }

    @PostMapping("/column")
    public String changeColumnPost(@Valid @ModelAttribute("columnDto") ChangeColumnDto columnDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return "/column/changeColumn";
        }

        return "redirect:./selectColumn";
    }

    /**
     * @param model  : 이전 페이지에서 생성한 model key-value 쌍을 활용할 예정. 그래서 객체를 새로 넣어줄 필요는 없음.
     *                  하지만 view단 출력을 위해 변수를 넣어줘야 하기에, 변수를 넣기 위한 addAttribute가 필요함.
     */
    @GetMapping("/selectColumn")
    public String selectColumn(Model model) {
        // model.addAttribute("columnDto", ChangeColumnDto.builder().build());

        return "/column/selectChangeColumn";
    }

    @PostMapping("/selectColumn")
    public String selectColumnPost(@Valid @ModelAttribute("columnDto") ChangeColumnDto columnDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "/column/selectChangeColumn";
        }


    }

}
