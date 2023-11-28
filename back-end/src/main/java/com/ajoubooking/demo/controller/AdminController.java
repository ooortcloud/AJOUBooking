package com.ajoubooking.demo.controller;

import com.ajoubooking.demo.dto.admin.AdminDto;
import com.ajoubooking.demo.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/login")  // 헤드 엔드포인트 설정
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("")
    public String loginPage(@ModelAttribute AdminDto dto, Model model) {  // 객체에 맞게 요청 파라미터 값들을 자동으로 바인딩
        model.addAttribute("dto", AdminDto.builder().build());  // 빈 객체를 넣어서 th:object에 전달해줘야 함
        return "newLoginPage";
    }

    @PostMapping("")
    public String validation(@Valid @ModelAttribute("dto") AdminDto dto, BindingResult bindingResult) {  // Bean Validation 적용
        // 아예 입력 형식이 잘못된 경우 돌려보냄. 에러 메세지는 처리된 예외에 맞춰 자동으로 배정됨.
        if(bindingResult.hasErrors()){
            System.out.println("글로벌 오류");
            ObjectError badInputError = new ObjectError("globalError", "PW 입력이 잘못되었습니다.");
            return "redirect:/login";
        }

        // 계정 조회에 실패한 경우 돌려보냄
        boolean b = adminService.validateRequestLogin(dto.getPw());
        if(!b){
            System.out.println("계정 조회 오류");
            bindingResult.reject("NotBlank.login");  // 에러 메세지 직접 지정
            return "redirect:/login";
        }

        return "redirect:/login/main";  // 리다이랙트로 새 페이지를 띄우지 않고 현재 페이지에서 곧바로 이동시킴. 유효성 검사 필요
    }

    @GetMapping("/main")
    public String adminMain() {
        return "adminMain";
    }

    @GetMapping("/change")
    public String checkIdPw(@ModelAttribute AdminDto dto, Model model) {
        model.addAttribute("dto", AdminDto.builder().build());
        return "changePw1";
    }

    // changePw2로 가는 경우 유효성 검사 필요..
    @PostMapping("/change")
    public String changePw(@Valid @ModelAttribute("dto") AdminDto dto, BindingResult bindingResult) {

        if(true)
            return "redirect:/login";
        return "redirect:/login/change";
    }
}
