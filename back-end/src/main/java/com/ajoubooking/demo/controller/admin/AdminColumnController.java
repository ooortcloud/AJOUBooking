package com.ajoubooking.demo.controller.admin;

import com.ajoubooking.demo.domain.Bookshelf;
import com.ajoubooking.demo.dto.admin.ButtonDto;
import com.ajoubooking.demo.dto.admin.ChangeColumnDto;
import com.ajoubooking.demo.dto.home.CallNumberDto;
import com.ajoubooking.demo.dto.home.ColumnAddressResponseDto;
import com.ajoubooking.demo.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(value = "/selectColumn/test")
    public String selectColumnPostTest(@RequestParam("buttonId") String buttonId) {
        /**
         *  누른 버튼에 따라 변경할 column을 바꿔줘야 함.  << javascript 상에서 버튼 id를 구분하여 ajax로 비동기통신을 해서 api 요청 처리를 해야 함...
         *  크기 비교해서 예외처리 해야 함  << 이거도 javascript 상에서 분기문 걸어서 처리해야 할듯... (alert 함수로 예외처리하자.)
         */
        if(buttonId.equals("nextcol")) {  // "다음" 쪽 버튼이 눌린 경우
            // 예외처리 메소드

            CallNumberDto nextCallNumberDto = adminService.separateRequestCallNumber(nextCallNumber);
            CallNumberDto inputCallNumberDto = adminService.separateRequestCallNumber(inputCallNumber);
            adminService.updateCallNumber(nextCallNumberDto, inputCallNumberDto);
        } else if(buttonId.equals("precol")) {  // "현재" 쪽 버튼이 눌린 겨우
            CallNumberDto presentCallNumberDto = adminService.separateRequestCallNumber(presentCallNumber);
            CallNumberDto inputCallNumberDto = adminService.separateRequestCallNumber(inputCallNumber);
            adminService.updateCallNumber(presentCallNumberDto, inputCallNumberDto);
        }


        return "/column/successChangeColumn";
    }




    // 신규 bookshelf를 추가하는 api

    // 기존 bookshelf를 제거하는 api
}
