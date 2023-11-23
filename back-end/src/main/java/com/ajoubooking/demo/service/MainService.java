package com.ajoubooking.demo.service;

import com.ajoubooking.demo.domain.Bookshelf;
import com.ajoubooking.demo.dto.CallNumberDto;
import com.ajoubooking.demo.dto.ColumnAddressResponseDto;
import com.ajoubooking.demo.dto.SeparatedAuthorSymbolDto;
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

    public ColumnAddressResponseDto binarySearch(CallNumberDto callNumberDto, Float low_num, Float high_num) {
        Float foundNum = binarySearchForClassification(callNumberDto.getClassificationNumber(), low_num, high_num);

        List<Bookshelf> foundAuthorSymbols = bookshelfRepository.findAllByStartCallNumberClassificationNumber(foundNum);
        ColumnAddressResponseDto answer = binarySearchForAuthor(callNumberDto.getAuthorSymbol(), foundAuthorSymbols);

        return answer;
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

    private ColumnAddressResponseDto binarySearchForAuthor(String key, List<Bookshelf> list) {
        int lowIndex = 0;
        int highIndex = list.size() - 1;
        Integer midIndex = null;

        SeparatedAuthorSymbolDto separatedKeyAuthorSymbol = separateAuthorSymbol(key);
        SeparatedAuthorSymbolDto separatedCompareAuthorSymbol = null;

        /*
        while(low <= high) {
            mid = (low + high) / 2;



            if()
        }
         */


        return null;
    }

    private SeparatedAuthorSymbolDto separateAuthorSymbol(String authorSymbol) {  // authorSymbol : B187hK한
        int n = authorSymbol.length();
        Character c;

        Character authorInit = authorSymbol.charAt(0);
        Character bookInit = null;

        int i;
        String num = "";  // null로 초기화하면, 문자열 합성 시 null이 들어가서 안됨
        String temp;
        for (i = 1; i < 4; i++) {
            c = authorSymbol.charAt(i);
            temp = c.toString();
            try {
                Integer.parseInt(temp);
            } catch (NumberFormatException e) {
                break;
            } finally {
                num = num + temp;
            }
        }

        bookInit = authorSymbol.charAt(i);

        return SeparatedAuthorSymbolDto.builder()
                .authorInitialConsonant(authorInit)
                .number(Integer.parseInt(num))
                .bookInitialConsonant(bookInit)
                .build();
    }
}
