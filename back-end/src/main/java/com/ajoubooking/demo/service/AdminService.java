package com.ajoubooking.demo.service;

import com.ajoubooking.demo.domain.Bookshelf;
import com.ajoubooking.demo.dto.admin.AdminDto;
import com.ajoubooking.demo.dto.home.CallNumberDto;
import com.ajoubooking.demo.dto.home.ColumnAddressResponseDto;
import com.ajoubooking.demo.repository.admin.AdminRepository;
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

    public boolean validateRequestLogin(String pw) {
        AdminDto foundAdmin = adminRepository.findByPw(pw);
        // 계정을 찾을 수 없으면 예외처리
        if (foundAdmin == null) return false;
        return true;
    }

    public void changePw(String newPw) {
        adminRepository.updatePw(newPw);
    }

    public Bookshelf findPreviousBookshelfByCallNumber(String callNumber) {
        return searchService.findPreviousBookshelfByCallNumber(callNumber);
    }

    /**
     * 알고리즘 작동 방식
     * 1. 먼저 파라미터와 동일한 category와 bookshelfNum을 갖는 row들 중에서, 자신보다 +1 큰 columnNum이 있는지 찾는다.
     * 2. 있으면 그게 nextCallNumber가 됨.
     * 3. 없으면 본인이 해당 책장의 마지막 시작 청구기호가 되는 것이므로, 항상 다음 책장의 첫번째 시작 청구기호가 nextCallNumber가 됨.
     * 3-1. 골때리게 해당 책장이 해당 category의 마지막 bookshelfNum이었다면, 다음 category의 최초 시작 청구기호를 찾아야 함.
     */
    public Bookshelf findNextBookshelfByColumnAddressDto(ColumnAddressResponseDto previous) {
        Integer previousCategory = previous.getCategory();
        Integer previousBookshelfNum = previous.getBookshelfNum();
        Integer previousColumnNum = previous.getColumnNum();
        Bookshelf getBookshelf = searchService.findBookshelfByColumnAddress(ColumnAddressResponseDto.builder()
                .category(previousCategory)
                .bookshelfNum(previousBookshelfNum)
                .columnNum(previousColumnNum + 1)
                .build());
        Optional<Bookshelf> temp;
        if (getBookshelf == null)
            temp = Optional.empty();
        else
            temp = Optional.of(getBookshelf);


        // 해당 책장의 마지막 column이었을 경우 예외처리 << 다음 책장의 첫번째 청구기호를 조회해야 함
        if (temp.isEmpty()) {
            Optional<Bookshelf> tempNextBookshelfNum;
            Bookshelf getBookshelf2 = searchService.findBookshelfByColumnAddress(ColumnAddressResponseDto.builder()
                    .category(previousCategory)
                    .bookshelfNum(previousBookshelfNum + 1)
                    .columnNum(1)
                    .build());
            if (getBookshelf2 == null)
                tempNextBookshelfNum = Optional.empty();
            else
                tempNextBookshelfNum = Optional.of(getBookshelf2);


            // 해당 category의 마지막 책장의 마지막 column이었을 경우 예외처리 << 다음 카테고리의 최초 청구기호를 조회해야 함
            if (tempNextBookshelfNum.isEmpty()) {
                Integer nextCategory = searchService.findNextCategory(previousCategory);
                Optional<Bookshelf> next;
                Bookshelf getBookshelf3 = searchService.findBookshelfByColumnAddress(ColumnAddressResponseDto.builder()
                        .category(nextCategory)
                        .bookshelfNum(1)
                        .columnNum(1)
                        .build());
                if (getBookshelf3 == null)
                    next = Optional.empty();
                else
                    next = Optional.of(getBookshelf3);


                // previous 책장이 해당 도서관의 마지막 category의 마지막 책장의 마지막 column이었을 때 예외처리
                if (next.isEmpty()) {
                    throw new IndexOutOfBoundsException("이전 책장이 현 도서관의 가장 마지막 column 시작 청구기호 입니다.");
                }

                return next.get();
            }

            return tempNextBookshelfNum.get();
        }

        return temp.get();
    }

    public CallNumberDto separateRequestCallNumber(String callNumber){
        return searchService.separateRequestCallNumber(callNumber);
    }

    public void updateCallNumber(CallNumberDto presentCallNumberDto, CallNumberDto inputCallNumberDto) {
        adminRepository.updateCallNumber(presentCallNumberDto, inputCallNumberDto);
    }


}