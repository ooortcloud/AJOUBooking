package com.ajoubooking.demo.service;

import com.ajoubooking.demo.domain.Bookshelf;
import com.ajoubooking.demo.dto.home.CallNumberDto;
import com.ajoubooking.demo.dto.home.ColumnAddressResponseDto;
import com.ajoubooking.demo.repository.bookshelf.BookshelfRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional  // 프록시 객체를 생성하여 자동 commit, rollback 등의 트랜잭션 처리
public class HomeService {
    private final BookshelfRepository bookshelfRepository;

    private final SearchService searchService;

    public HomeService(BookshelfRepository bookshelfRepository, SearchService searchService) {
        this.bookshelfRepository = bookshelfRepository;
        this.searchService = searchService;
    }

    public CallNumberDto separateRequestCallNumber(String callNumber) {
        return searchService.separateRequestCallNumber(callNumber);
    }

    /*
    public Optional<ColumnAddressResponseDto> binarySearchForResponse(CallNumberDto requestedCallNumber) {
        Bookshelf ans = searchService.binarySearch(requestedCallNumber);
        return Optional.of(ColumnAddressResponseDto.builder()
                        .category(ans.getColumnAddress().getCategory())
                        .bookshelfNum(ans.getColumnAddress().getBookshelfNum())
                        .columnNum(ans.getColumnAddress().getColumnNum())
                        .build());
    }
     */

    public ColumnAddressResponseDto binarySearchForResponse(CallNumberDto callNumberDto) {
        return searchService.binarySearchForResponse(callNumberDto);
    }
}
