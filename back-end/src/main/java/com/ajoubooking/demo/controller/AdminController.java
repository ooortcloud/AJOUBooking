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
    public String loginPage(Model model) {
        model.addAttribute("dto", AdminDto.builder().build());  // th:object에 정의한 빈 객체를 넣어 전달해줘야 함
        return "newLoginPage";
    }

    /**
     * @ModelAttribute의 디폴트 규칙은 도메인명과 동일하게 필드 이름을 갖는 것. 그 이름을 쓰기 싫으면 직접 이름을 지정해줘야 한다.
     * 참고) @ModelAttribute는 HTTP 요청 파라미터를 처리할 때 사용한다. URL 쿼리 스트링, POST form을 처리하는 것이 대표적.
     *
     * BindingResult 인터페이스를 받아서 Bean Validation 적용
     *
     * @return 오류 뜨면 오류 메시지를 출력, 로그인 성공하면 세션을 생성하며 메인 페이지로 이동시킴.
     */
    @PostMapping("")
    public String validation(@Valid @ModelAttribute("dto") AdminDto dto, BindingResult bindingResult) {

        // AdminDto에 걸어둔 유효성 검사에 위반되면, bindingResult 객체에 dto에서 설정해둔 메세지를 담아서 리턴.
        if(bindingResult.hasErrors()){
            return "newLoginPage";
        }

        // 계정 조회에 실패한 경우 돌려보냄
        boolean b = adminService.validateRequestLogin(dto.getPw());
        if(!b){
            System.out.println("계정 조회 오류");
            bindingResult.rejectValue("pw", null, "pw 입력이 잘못되었습니다.");
            /**
             * bindingResult.rejectValue()  << 필드 에러
             * bindingResult.reject()  << 객체 에러
             * errorCode : 'errors.properties'에 정의한 매크로 메세지를 읽어옴. null인 경우 설정된 defaultMessage를 넘김.
             */
            return "newLoginPage";
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