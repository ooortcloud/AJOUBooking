package com.ajoubooking.demo.controller.admin;

import com.ajoubooking.demo.domain.Bookshelf;
import com.ajoubooking.demo.dto.admin.ChangeColumnDto;
import com.ajoubooking.demo.dto.home.ColumnAddressDto;
import com.ajoubooking.demo.service.AdminService;
import jakarta.validation.Valid;
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

    private final AdminService adminService;

    public AdminColumnController(AdminService adminService) {
        this.adminService = adminService;
    }

    private static String inputCallNumber;

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
        
        // view로부터 입력받은 값을 클래스 단에 임시로 저장해두기 위함
        inputCallNumber = columnDto.getInputCallNumber();

        return "redirect:./selectColumn";
    }

    /**
     * @param model  : 이전 페이지에서 생성한 model key-value 쌍을 활용할 예정. 그래서 객체를 새로 넣어줄 필요는 없음.
     *                  하지만 view단 출력을 위해 변수를 넣어줘야 하기에, 변수를 넣기 위한 addAttribute가 필요함.
     */
    @GetMapping("/selectColumn")
    public String selectColumn(Model model) {
        // model.addAttribute("columnDto", ChangeColumnDto.builder().build());
        Bookshelf temp = adminService.findPreviousBookshelfByCallNumber(inputCallNumber);
        String previousCallNumber = temp.getStartCallNumber().getClassificationNumber() + temp.getStartCallNumber().getAuthorSymbol();
        model.addAttribute("previous", previousCallNumber);
        model.addAttribute("present", inputCallNumber);

        model.addAttribute("next", adminService.findNextBookshelfByColumnAddressDto(ColumnAddressDto.builder()
                        .category(temp.getColumnAddress().getCategory())
                        .bookshelfNum(temp.getColumnAddress().getBookshelfNum())
                        .columnNum(temp.getColumnAddress().getColumnNum())
                        .build()));
        return "/column/selectChangeColumn";
    }

    @PostMapping("/selectColumn")
    public String selectColumnPost(@Valid @ModelAttribute("columnDto") ChangeColumnDto columnDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "/column/selectChangeColumn";
        }

        Bookshelf temp = adminService.findPreviousBookshelfByCallNumber(columnDto.getInputCallNumber());
        try {
            Bookshelf nextBookshelf = adminService.findNextBookshelfByColumnAddressDto(ColumnAddressDto.builder()
                    .category(temp.getColumnAddress().getCategory())
                    .bookshelfNum(temp.getColumnAddress().getBookshelfNum())
                    .columnNum(temp.getColumnAddress().getColumnNum())
                    .build());
        } catch (IndexOutOfBoundsException e) {
            // MVC 모델에서 어떻게 상태코드를 설정하고 에러 메세지를 전송하지??
            return "";
        }

        return "/column/selectChangeColumn";
    }

}
