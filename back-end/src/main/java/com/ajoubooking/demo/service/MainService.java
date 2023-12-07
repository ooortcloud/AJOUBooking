package com.ajoubooking.demo.service;

import com.ajoubooking.demo.domain.Bookshelf;
import com.ajoubooking.demo.dto.home.CallNumberDto;
import com.ajoubooking.demo.dto.home.ColumnAddressResponseDto;
import com.ajoubooking.demo.dto.home.SeparatedAuthorSymbolDto;
import com.ajoubooking.demo.repository.bookshelf.BookshelfDataRepository;
import com.ajoubooking.demo.repository.bookshelf.BookshelfRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional  // 프록시 객체를 생성하여 자동 commit, rollback 등의 트랜잭션 처리
public class MainService {

    private final BookshelfRepository bookshelfRepository;

    public MainService(BookshelfRepository bookshelfRepository) {
        this.bookshelfRepository = bookshelfRepository;
    }


    public CallNumberDto separateRequestCallNumber(String callNumber){  // 예외처리를 하기 위해 throws 사용
        String[] s = callNumber.split(" ");

        BigDecimal bigDecimal = null;
        CallNumberDto callNumberDto = null;
        
        int checkLen = s.length;
        // 잘못된 입력 양식에 대해서는 예외처리
        if(checkLen > 3 || checkLen <= 1) {
            throw new InputMismatchException("청구기호의 기본 양식을 벗어났습니다.");
        }

        // 별치기호 입력된 경우 별개 처리
        int i = 0;
        if (checkLen == 3) {
            i++;
        }
        try {
            bigDecimal = BigDecimal.valueOf(Double.valueOf(s[i]));  // Long은 String 타입 변환 지원 안함
        } catch (NumberFormatException e) {
            throw new NumberFormatException("분류번호는 반드시 숫자여야 합니다.");
        }

        callNumberDto = CallNumberDto.builder()
                .classificationNumber(bigDecimal)
                .authorSymbol(s[i+1])
                .build();

        return callNumberDto;
    }

    public Optional<ColumnAddressResponseDto> binarySearchForResponse(CallNumberDto callNumberDto) {
        /*  예외처리를 못하는 코드라 제거함
        Bookshelf foundRow = bookshelfRepository
                .findFirstByStartCallNumberClassificationNumberLessThanEqualOrderByStartCallNumberClassificationNumberDesc(callNumberDto.getClassificationNumber());

         */

        /**
         *  입력된 청구기호의 분류번호가 조회된 row 중 가장 큰 분류번호와 일치할 때 예외처리:
         *  1) 입력된 분류번호 값과 first가 서로 동일한 경우를 분기문으로 처리
         *  2) 분류번호가 first인 boolshelf 중 맨 처음 column인 bookshelf를 가져옴.
         *  3) 분류번호가  second인 bookshelf들 중에서, 가장 마지막 column인 bookshelf를 가져옴.
         *  4) 만약 저 두 bookshelf 사이에 입력된 청구기호가 들어간다면, 해당 서적은 반드시 3)에 해당하는 column에 존재함.
         *  5) 그렇지 않다면, 분류번호값이 first인 것들 중에 대해 기존 조회 로직으로 재탐색.
         */
        List<Bookshelf> foundRows = bookshelfRepository.findTopTwoLessThanEqualClassificationNumber(callNumberDto.getClassificationNumber());

        // 도서관 내 가장 작은 청구기호보다 작은 값을 입력했을 때 예외처리
        if(foundRows.isEmpty())
            throw new InputMismatchException("존재할 수 없는 서적입니다. 만약 존재하는 서적이라면 관리자에게 문의해주세요.");

        if(foundRows.get(0).getStartCallNumber().getClassificationNumber() == callNumberDto.getClassificationNumber()) {

        }
        
        List<Bookshelf> foundAuthorSymbols = bookshelfDataRepository.findByStartCallNumberClassificationNumber(
                foundRow.getStartCallNumber().getClassificationNumber());
        /*  이 분기문을 넣어버리면 오히려 예외처리를 안하게 되어버림.
        // 결과값이 1개뿐이어서 이진탐색을 할 필요가 없는 경우
        if (foundAuthorSymbols.size() == 1) {
            ColumnAddress answer = foundAuthorSymbols.get(0).getColumnAddress();
            return Optional.of(ColumnAddressResponseDto.builder()
                    .category(answer.getCategory())
                    .bookshelfNum(answer.getBookshelfNum())
                    .columnNum(answer.getColumnNum())
                    .build());
        }
        else

         */
        return binarySearchForAuthor(callNumberDto.getAuthorSymbol(), foundAuthorSymbols);
    }

    private Optional<ColumnAddressResponseDto> binarySearchForAuthor(String key, List<Bookshelf> foundAuthorSymbols) {

        int lowIndex = 0;
        int highIndex = foundAuthorSymbols.size() - 1;
        Integer midIndex = null;

        // 섹션 별로 효율적인 이진탐색을 진행하기 위해 조회된 모든 row에 대해 저자기호를 전부 분리함
        List<SeparatedAuthorSymbolDto> separatedAuthorSymbols = new ArrayList<>();
        for (Bookshelf bookshelf : foundAuthorSymbols) {
            separatedAuthorSymbols.add(separateAuthorSymbol(bookshelf.getStartCallNumber().getAuthorSymbol()));
        }

        SeparatedAuthorSymbolDto separatedKeyAuthorSymbol = separateAuthorSymbol(key);

        // 1차 : 저자 초성에 대해 이진탐색
        Character myKey = separatedKeyAuthorSymbol.getAuthorInitialConsonant();
        Character myMid = null;
        boolean keyIsKorean = true;
        boolean midIsKorean = true;
        while(lowIndex <= highIndex) {
            midIndex = (lowIndex + highIndex) / 2;
            myMid = separatedAuthorSymbols.get(midIndex).getAuthorInitialConsonant();

            // 청구기호와 컴퓨터의 영어, 한글 대소관계가 정반대라서 각 케이스에 대한 처리가 필요
            if('A' <= myKey && myKey <= 'z') keyIsKorean = false;
            else keyIsKorean = true;
            if('A' <= myMid && myMid <= 'z') midIsKorean = false;
            else midIsKorean = true;
            if(keyIsKorean && !midIsKorean) {  // key < mid
                highIndex = midIndex - 1;
            } else if (!keyIsKorean && midIsKorean) {  // key > mid
                lowIndex = midIndex + 1;
            }

            else {
                if(myKey < myMid) {
                    highIndex = midIndex - 1;
                } else if (myKey > myMid) {
                    lowIndex = midIndex + 1;
                } else {
                    break;
                }
            }
        }
        Character setAuthorInit = myMid;

        // 2차 : 숫자에 대해 이진탐색
        // 예상되는 리스트 길이가 매우 짧을 것으로 예상되므로 순차탐색을 진행함
        List<SeparatedAuthorSymbolDto> temp = new ArrayList<>();
        for (SeparatedAuthorSymbolDto authorSymbol : separatedAuthorSymbols) {
            if(authorSymbol.getAuthorInitialConsonant() == setAuthorInit)
                temp.add(authorSymbol);
        }
        // 저자 초성이 겹치는 경우 다음 레벨의 이진탐색을 진행
        if(temp.size() > 1) {
            lowIndex = 0;
            highIndex = temp.size() - 1;
            Integer myKey2 = separatedKeyAuthorSymbol.getNumber();
            Integer myMid2 = null;
            while (lowIndex <= highIndex) {
                midIndex = (lowIndex + highIndex) / 2;
                myMid2 = temp.get(midIndex).getNumber();
                if(myKey2 < myMid2) {
                    highIndex = midIndex - 1;
                } else if (myKey2 > myKey2) {
                    lowIndex = midIndex + 1;
                } else {
                    break;
                }
            }
        } else if (temp.size() == 1) {
            midIndex = 0;
        } else {
            System.exit(-1);  // 값이 없는 건 DB에 접근하지 못했을 때 뿐임. 심각한 에러라는 것.
        }
        Integer setNum = temp.get(midIndex).getNumber();

        // 3차 : 책 제목 초성에 대해 이진탐색
        List<SeparatedAuthorSymbolDto> temp2 = new ArrayList<>();
        for (SeparatedAuthorSymbolDto authorSymbol : temp) {
            if(authorSymbol.getNumber() == setNum)
                temp2.add(authorSymbol);
        }
        // 책 제목 초성이 겹치는 경우 마지막 레벨의 이진탐색을 진행
        if(temp2.size() > 1) {
            lowIndex = 0;
            highIndex = temp2.size() - 1;
            myKey = separatedKeyAuthorSymbol.getBookInitialConsonant();
            while (lowIndex <= highIndex) {
                midIndex = (lowIndex + highIndex) / 2;
                myMid = temp2.get(midIndex).getBookInitialConsonant();
                if('A' <= myKey && myKey <= 'z') keyIsKorean = false;
                else keyIsKorean = true;
                if('A' <= myMid && myMid <= 'z') midIsKorean = false;
                else midIsKorean = true;
                if(keyIsKorean && !midIsKorean) {  // key < mid
                    highIndex = midIndex - 1;
                } else if (!keyIsKorean && midIsKorean) {  // key > mid
                    lowIndex = midIndex + 1;
                }

                else {
                    if(myKey < myMid) {
                        highIndex = midIndex - 1;
                    } else if (myKey > myMid) {
                        lowIndex = midIndex + 1;
                    } else {
                        break;
                    }
                }
            }
        } else if(temp2.size() == 1) {
            midIndex = 0;
        } else{
            System.exit(-1);
        }
        Character setBookInit = temp2.get(midIndex).getBookInitialConsonant();

        // 최종적으로 결정된 조각들을 전부 조합
        String answer = setAuthorInit + String.valueOf(setNum) + setBookInit;

        // body에 채워넣을 객체값 만들기
        ColumnAddressResponseDto result = buildBookshelfAuthorSymbolToColumnAddressResponseDto(foundAuthorSymbols, answer);
        if (result == null)
            return Optional.empty();
        else
            return Optional.of(result);
    }

    private ColumnAddressResponseDto buildBookshelfAuthorSymbolToColumnAddressResponseDto(
            List<Bookshelf> bookshelfList, String answer) {
        for (Bookshelf bookshelf : bookshelfList) {
            if(bookshelf.getStartCallNumber().getAuthorSymbol().contains(answer)) {
                return ColumnAddressResponseDto.builder()
                        .category(bookshelf.getColumnAddress().getCategory())
                        .bookshelfNum(bookshelf.getColumnAddress().getBookshelfNum())
                        .columnNum(bookshelf.getColumnAddress().getColumnNum())
                        .build();
            }
        }

        return null;
    }

    private SeparatedAuthorSymbolDto separateAuthorSymbol(String authorSymbol) {  // authorSymbol : B187hK한
        int n = authorSymbol.length();
        Character c;

        Character authorInit = authorSymbol.charAt(0);
        Character bookInit = null;

        int i;  // for 문 내 변수들은 for문이 종료되면 소멸됨. 재활용하기 위해서 밖으로 꺼내 둠.
        String num = "";  // null로 초기화하면, 문자열 합성 시 null이 들어가서 안됨
        String temp;
        for (i = 1; i < 4; i++) {
            c = authorSymbol.charAt(i);
            temp = c.toString();
            try {
                Integer.parseInt(temp);
            } catch (NumberFormatException e) {
                break;  // 문자를 만나면 파싱 종료
            }
            num = num + temp;
        }

        bookInit = authorSymbol.charAt(i);
        
        // 저자기호가 전부 문자인 경우 예외처리
        try{
            return SeparatedAuthorSymbolDto.builder()
                    .authorInitialConsonant(authorInit)
                    .number(Integer.parseInt(num))
                    .bookInitialConsonant(bookInit)
                    .build();
        } catch (Exception e) {
            throw new InputMismatchException("저자기호 입력 양식이 잘못되었습니다.");
        }
    }
}
