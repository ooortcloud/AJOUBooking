package com.ajoubooking.demo.service;

import com.ajoubooking.demo.domain.Bookshelf;
import com.ajoubooking.demo.dto.CallNumberDto;
import com.ajoubooking.demo.repository.BookshelfRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional  // 프록시 객체를 생성하여 자동 commit, rollback 등의 트랜잭션 처리
public class MainService {

    private final BookshelfRepository bookshelfRepository;

    public MainService(BookshelfRepository bookshelfRepository) {
        this.bookshelfRepository = bookshelfRepository;
    }


    public CallNumberDto separateRequestCallNumber(String callNumber) {
        String[] s = callNumber.split(" ");

        int i = 0;
        Float f = null;

        // 별치기호 예외처리
        try {
            f = Float.parseFloat(s[0]);
        } catch (NumberFormatException e) {
            i++;
        } finally {
            f = Float.parseFloat(s[i]);
        }

        CallNumberDto callNumberDto = CallNumberDto.builder()
                .classificationNumber(f)
                .authorSymbol(s[i+1])
                .build();

        return callNumberDto;
    }

    public void binarySearch(CallNumberDto callNumberDto, Float low_num, Float high_num) {
        Float foundNum = binarySearchForClassification(callNumberDto.getClassificationNumber(), low_num, high_num);

        List<Bookshelf> foundAuthorSymbols = bookshelfRepository.findAllByStartCallNumberClassificationNumber(foundNum);
        String foundSym = binarySearchForAuthor(callNumberDto.getAuthorSymbol(), foundAuthorSymbols);
    }

    private Float binarySearchForClassification(Float key, Float low, Float high) {


        Float mid = null;

        while(low <= high) {
            mid = (low + high) / 2;

            if(key < mid) {
                high = mid;
            } else if (key > mid) {
                low = mid;
            } else {
                return mid;
            }
        }
        return -1f;
    }

    private String binarySearchForAuthor(String key, List<Bookshelf> list) {
        int low = 0;
        int high = list.size() - 1;
        Integer mid = null;

        Map<String, Object> separatedAuthorSymbol = separateAuthorSymbol(key);

//        while(low <= high) {
//            mid = (low + high) / 2;
//
//
//
//            if()
//        }

        return "";
    }

    private Map<String, Object> separateAuthorSymbol(String author_symbol) {
        int n = author_symbol.length();
        Character c = null;

        Map<String, Object> temp = new HashMap<>();
        for (int i = 0; i < n; i++) {
            c = author_symbol.charAt(i);

        }

        return temp;
    }
}
