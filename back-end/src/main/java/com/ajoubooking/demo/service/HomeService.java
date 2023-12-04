package com.ajoubooking.demo.service;

import com.ajoubooking.demo.domain.Bookshelf;
import com.ajoubooking.demo.dto.home.CallNumberDto;
import com.ajoubooking.demo.dto.home.ColumnAddressDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional  // 프록시 객체를 생성하여 자동 commit, rollback 등의 트랜잭션 처리
public class HomeService {

    private final SearchService searchService;

    public HomeService(SearchService searchService) {
        this.searchService = searchService;
    }

    public CallNumberDto separateRequestCallNumber(String callNumber) {
        return searchService.separateRequestCallNumber(callNumber);
    }

    public Optional<ColumnAddressDto> binarySearchForResponse(CallNumberDto requestedCallNumber) {
        Bookshelf ans = searchService.binarySearch(requestedCallNumber);
        return Optional.of(ColumnAddressDto.builder()
                        .category(ans.getColumnAddress().getCategory())
                        .bookshelfNum(ans.getColumnAddress().getBookshelfNum())
                        .columnNum(ans.getColumnAddress().getColumnNum())
                        .build());
    }
}
