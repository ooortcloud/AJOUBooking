package com.ajoubooking.demo.controller.admin;

import com.ajoubooking.demo.domain.Bookshelf;
import com.ajoubooking.demo.dto.admin.ChangeColumnDto;
import com.ajoubooking.demo.dto.home.ColumnAddressResponseDto;
import com.ajoubooking.demo.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@Controller
@RequestMapping("/login")
public class AdminColumnController {

    private final AdminService adminService;

    public AdminColumnController(AdminService adminService) {
        this.adminService = adminService;
    }

    private static String inputCallNumber;
    private static String previousCallNumber;
    private static String presentCallNumber;
    private static String nextCallNumber;

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
        model.addAttribute("inputVal", inputCallNumber);  // 앞에서 입력한 청구기호

        // 이전 column 시작 청구기호
        Bookshelf tempPrevious = adminService.findPreviousBookshelfByCallNumber(inputCallNumber);
        BigDecimal getClassificationNum;
        // 최초값을 수정하는 경우 예외처리
        if(tempPrevious == null) {
            model.addAttribute("previous", "");
        } else {
            getClassificationNum = tempPrevious.getStartCallNumber().getClassificationNumber().stripTrailingZeros();  // 소수점 이하 불필요한 0들을 제거하기 표현하기 위함
            previousCallNumber = getClassificationNum + " " + tempPrevious.getStartCallNumber().getAuthorSymbol();
            model.addAttribute("previous", previousCallNumber);
        }

        // 현재 column 시작 청구기호
        Bookshelf tempPresent = adminService.findNextBookshelfByColumnAddressDto(ColumnAddressResponseDto.builder()
                .category(tempPrevious.getColumnAddress().getCategory())
                .bookshelfNum(tempPrevious.getColumnAddress().getBookshelfNum())
                .columnNum(tempPrevious.getColumnAddress().getColumnNum())
                .build());
        getClassificationNum = tempPresent.getStartCallNumber().getClassificationNumber().stripTrailingZeros();
        presentCallNumber = getClassificationNum + " " + tempPresent.getStartCallNumber().getAuthorSymbol();
        model.addAttribute("present", presentCallNumber);

        // 다음 column 시작 청구기호
        Bookshelf tempNext = adminService.findNextBookshelfByColumnAddressDto(ColumnAddressResponseDto.builder()
                .category(tempPresent.getColumnAddress().getCategory())
                .bookshelfNum(tempPresent.getColumnAddress().getBookshelfNum())
                .columnNum(tempPresent.getColumnAddress().getColumnNum())
                .build());
        // 마지막 값을 수정하는 경우 예외처리
        if(tempNext == null) {
            model.addAttribute("next", "");
        } else {
            getClassificationNum = tempNext.getStartCallNumber().getClassificationNumber().stripTrailingZeros();
            nextCallNumber = getClassificationNum + " " + tempNext.getStartCallNumber().getAuthorSymbol();
            model.addAttribute("next", nextCallNumber);
        }

        return "/column/selectChangeColumn";
    }

    @PostMapping("/selectColumn")
    public String selectColumnPost(@Valid @ModelAttribute("columnDto") ChangeColumnDto columnDto, BindingResult bindingResult) {

        // 생각해보니 매번 조회할 때마다 새로운 이전 이후 column을 조회하기 때문에 범위를 넘는 에러가 발생할 일이 없다.

        /*
        if(bindingResult.hasErrors()) {
            return "/column/selectChangeColumn";
        }

        Bookshelf temp = adminService.findPreviousBookshelfByCallNumber(columnDto.getInputCallNumber());
        try {
            Bookshelf nextBookshelf = adminService.findNextBookshelfByColumnAddressDto(ColumnAddressResponseDto.builder()
                    .category(temp.getColumnAddress().getCategory())
                    .bookshelfNum(temp.getColumnAddress().getBookshelfNum())
                    .columnNum(temp.getColumnAddress().getColumnNum())
                    .build());
        } catch (IndexOutOfBoundsException e) {
            // MVC 모델에서 어떻게 상태코드를 설정하고 에러 메세지를 전송하지??
            return "";
        }

         */

        return "/column/selectChangeColumn";
    }

}
