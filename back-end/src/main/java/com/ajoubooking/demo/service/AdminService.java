package com.ajoubooking.demo.service;

import com.ajoubooking.demo.dto.admin.AdminDto;
import com.ajoubooking.demo.dto.home.CallNumberDto;
import com.ajoubooking.demo.dto.home.ColumnAddressResponseDto;
import com.ajoubooking.demo.repository.AdminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AdminService {

    /**
     * 의존성 주입 방법 2가지
     * 1. @Autowired를 활용한 필드 주입
     * 2. 생성자 주입
     * 생성자 주입이 순환 안정성과 테스트 확장성 모두 좋기 때문에, 의존성 주입을 할 때는 가급적 생성자 주입을 사용하도록 하자.
      */
    private final AdminRepository adminRepository;
    private final SearchService searchService;

    public AdminService(AdminRepository adminRepository, SearchService searchService) {
        this.adminRepository = adminRepository;
        this.searchService = searchService;
    }

    public boolean validateRequestLogin(String pw){
        AdminDto foundAdmin = adminRepository.findByPw(pw);
        // 계정을 찾을 수 없으면 예외처리
        if(foundAdmin == null) return false;
        return true;
    }

    public void changePw(String newPw) {
        adminRepository.updatePw(newPw);
    }

    public CallNumberDto findPreviousCallNumber(String callNumber) {
        CallNumberDto callNumberDto = searchService.separateRequestCallNumber(callNumber);
        Optional<ColumnAddressResponseDto> columnAddressResponseDto = searchService.binarySearchForResponse(callNumberDto);

        return ;
    }

}
