package com.ajoubooking.demo.controller;

import com.ajoubooking.demo.dto.admin.AdminDto;
import com.ajoubooking.demo.dto.admin.ChangePwDto;
import com.ajoubooking.demo.dto.admin.CheckPwDto;
import com.ajoubooking.demo.service.AdminService;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

@Controller
@RequestMapping("/login")  // 헤드 엔드포인트 설정
/**
 * /login 엔드포인트에서 활동할 때는 항상 세션 검사를 통해 적절한 페이지를 제시해야 한다.
 */
public class AdminController {

    @Autowired
    private AdminService adminService;


    @GetMapping("/main")
    public String adminMain() {
        return "adminMain";
    }

    /**
     * @param model : view에 전달할 객체를 model에 addAttribute를 통해 저장해줘야 함.
     */
    @GetMapping("")
    public String loginPage(Model model) {
        model.addAttribute("dto", AdminDto.builder().build());  // th:object에 NoArg 객체를 model에 넣어서 view에 전달.
        return "newLoginPage";
    }

    /**
     * @ModelAttribute의 디폴트 규칙은 도메인명과 동일하게 필드 이름을 갖는 것. 그 이름을 쓰기 싫으면 직접 이름을 지정해줘야 한다.
     * 참고) @ModelAttribute는 HTTP 요청 파라미터를 처리할 때 사용한다. URL 쿼리 스트링, POST form을 처리하는 것이 대표적.
     *
     * @Valid가 적용된 파라미터는 자동으로 Bean Validation 유효성 검사를 거치게 됨. (Dto에 적용했던 @NotBlank라던가... 이런거 필터링됨)
     *
     * BindingResult 인터페이스를 받아서 Bean Validation 적용
     * 원하는 파라미터에 BindingResult를 적용하려면, 해당 파라미터 바로 뒤에 파라미터를 받아줘야 함.
     *
     * @return 오류 뜨면 오류 메시지를 출력, 로그인 성공하면 세션을 생성하며 메인 페이지로 이동시킴.
     */
    @PostMapping("")
    public String validation(@Valid @ModelAttribute("dto") AdminDto dto, BindingResult bindingResult) {

        // AdminDto에 걸어둔 유효성 검사에 위반되면, dto에서 설정해둔 오류 메세지를 bindingResult 객체에 담아서 view에 리턴.
        if(bindingResult.hasErrors()){
            return "newLoginPage";
        }

        // 계정 조회에 실패한 경우 에러 메시지를 담아서 view에 리턴.
        boolean b = adminService.validateRequestLogin(dto.getPw());
        if(!b){
            /**
             * bindingResult.rejectValue()  << 필드 에러
             * bindingResult.reject()  << 객체 에러
             * errorCode : 'errors.properties'에 정의한 매크로 메세지를 읽어옴. null인 경우 설정된 defaultMessage를 넘김.
             */
            bindingResult.rejectValue("pw", null, "pw 입력이 잘못되었습니다.");
            return "newLoginPage";
        }

        return "redirect:/login/main";  // 리다이랙트로 새 페이지를 띄우지 않고 현재 페이지에서 곧바로 이동시킴. 유효성 검사 필요
    }

    /**
     * @param model  : 새로운 객체를 전달하려면, 이전에 쓴 model 키값과 다른 이름으로 선언해줘야 thymeleaf에서 혼선을 일으키지 않는다.
     */
    @GetMapping("/beforeChangePw")
    public String checkPw(Model model) {
        model.addAttribute("beforeDto", CheckPwDto.builder().build());
        return "beforeChangePw";
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
            return "beforeChangePw";
        }

        if(!adminService.validateRequestLogin(beforeDto.getInputPw())) {
            bindingResult.rejectValue("inputPw", null, "pw 입력이 잘못되었습니다.");
            return "beforeChangePw";
        }

        return "redirect:./afterChangePw";
    }

    @GetMapping("/afterChangePw")
    public String changePw(Model model) {
        model.addAttribute("afterDto", ChangePwDto.builder().build());
        return "afterChangePw";
    }

    @PostMapping("/afterChangePw")
    public String changePwValidation(@Valid @ModelAttribute("afterDto") ChangePwDto afterDto, BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            return "afterChangePw";
        }

        // 두 입력값이 일치하지 않는 경우 예외처리
        if(!afterDto.getInputB().equals(afterDto.getInputA())) {
            bindingResult.rejectValue("inputB", null, "입력된 두 값이 서로 일치하지 않습니다.");
            return "afterChangePw";
        }

        adminService.changePw(afterDto.getInputB());

        // 해당 페이지에는 어떤 객체도 전달할 필요가 없기 때문에 thymeleaf와 충돌나지 않음 >> url 고정시키고 새 html 페이지만 렌더링.
        return "successChangePw";
    }
}
