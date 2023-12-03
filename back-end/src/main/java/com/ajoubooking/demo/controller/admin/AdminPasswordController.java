package com.ajoubooking.demo.controller.admin;

import com.ajoubooking.demo.dto.admin.ChangePwDto;
import com.ajoubooking.demo.dto.admin.CheckPwDto;
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
public class AdminPasswordController {

    @Autowired
    private AdminService adminService;

    /**
     * @param model  : 새로운 객체를 전달하려면, 이전에 쓴 model 키값과 다른 이름으로 선언해줘야 thymeleaf에서 혼선을 일으키지 않는다.
     */
    @GetMapping("/beforeChangePw")
    public String checkPw(Model model) {
        model.addAttribute("beforeDto", CheckPwDto.builder().build());
        return "/password/beforeChangePw";
    }

    /**
     * 비밀번호 변경 전 현재 실제 관리자가 제어하는 것이 맞는지 재확인하는 메소드.
     * @param beforeDto  : 앞서 model 키값을 변경했으니, 여기서도 동일한 값으로 파라미터명을 변경해줘야 modelAttirubute가 제대로 적용됨.
     *
     * @return redirect : 그냥 리턴하면 th:action에 따라 url이 고정되어버려서 에러남.
     *                      따라서 redirect로 엔드포인트를 바꿔줘야 의도된 Get 요청을 하게 됨.
     *                      참고로 redirect 주소에 상대 주소 규칙을 적용할 수 있음.
     */
    @PostMapping("/beforeChangePw")
    public String checkPwValidation(@Valid @ModelAttribute("beforeDto") CheckPwDto beforeDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return "/password/beforeChangePw";
        }

        if(!adminService.validateRequestLogin(beforeDto.getInputPw())) {
            bindingResult.rejectValue("inputPw", null, "pw 입력이 잘못되었습니다.");
            return "/password/beforeChangePw";
        }

        return "redirect:./afterChangePw";
    }

    @GetMapping("/afterChangePw")
    public String changePw(Model model) {
        model.addAttribute("afterDto", ChangePwDto.builder().build());
        return "/password/afterChangePw";
    }

    @PostMapping("/afterChangePw")
    public String changePwValidation(@Valid @ModelAttribute("afterDto") ChangePwDto afterDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return "/password/afterChangePw";
        }

        // 두 입력값이 일치하지 않는 경우 예외처리
        if(!afterDto.getInputB().equals(afterDto.getInputA())) {
            bindingResult.rejectValue("inputB", null, "입력된 두 값이 서로 일치하지 않습니다.");
            return "/password/afterChangePw";
        }

        adminService.changePw(afterDto.getInputB());

        // 해당 페이지에는 어떤 객체도 전달할 필요가 없기 때문에 thymeleaf와 충돌나지 않음 >> url 고정시키고 새 html 페이지만 렌더링.
        return "/password/successChangePw";
    }
}
