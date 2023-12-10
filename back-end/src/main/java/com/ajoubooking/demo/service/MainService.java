package com.ajoubooking.demo.service;

import com.ajoubooking.demo.domain.Bookshelf;
import com.ajoubooking.demo.dto.home.CallNumberDto;
import com.ajoubooking.demo.dto.home.ColumnAddressResponseDto;
import com.ajoubooking.demo.dto.home.SeparatedAuthorSymbolDto;
import com.ajoubooking.demo.repository.bookshelf.BookshelfRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

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

    public ColumnAddressResponseDto binarySearchForResponse(CallNumberDto callNumberDto) {

        /**
         * 알고리즘 리메이크
         * 1) 입력된 분류번호와 작거나 같은 분류번호 중 가장 큰 분류번호를 read
         * 2) 조회된 분류번호에 해당하는 모든 bookshelf들을 read
         * 3) 해당 bookshelf 리스트에 대해 입력된 저자번호에 대해서 이진탐색
         * 3-1) 이진탐색으로 선별된 column값이 정답.
         * 4-1) 만약 이진탐색으로 선별된 값이 없다면 입력된 저자번호가 가장 작은 저자번호보다 작다는 것이므로, 현재 분류번호보다 하나 앞선 bookshelf들을 read
         * 4-2) 해당 bookshelf 리스트 중 가장 끝에 있는 column값이 반드시 정답.
         */
        BigDecimal temp = bookshelfRepository.findTopClassificationNumberLessThanEqualOrderByDesc(callNumberDto.getClassificationNumber());
        List<Bookshelf> foundRows = bookshelfRepository.findByClassificationNumber(temp);

        // 도서관 내 가장 작은 청구기호보다 작은 값을 입력했을 때 예외처리
        if(foundRows.isEmpty())
            throw new InputMismatchException("존재할 수 없는 서적입니다. 만약 존재하는 서적이라면 관리자에게 문의해주세요.");

        Optional<ColumnAddressResponseDto> tempResult = binarySearchForAuthor(callNumberDto.getAuthorSymbol(), foundRows);

        /**
         *  예외가 터졌으면 예외 케이스 처리 로직 실행.
         *  그렇지 않으면 정답 column을 리턴.
         */
        if(tempResult.isEmpty()) {
            ColumnAddressResponseDto ans = catchExceptionCase(callNumberDto.getClassificationNumber());
            return ans;
        } else {
            return tempResult.get();
        }
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
        SeparatedAuthorSymbolDto getFirst = separatedAuthorSymbols.get(0);

        /**
         *  청구기호와 컴퓨터의 영어, 한글 대소관계가 정반대라서 각 케이스에 대한 처리가 필요
         *
         * @return : 만약 가장 작은 저자기호보다 입력된 저자기호가 더 작으면 예외처리.
         */
        boolean empty = checkExceptionCase(separatedKeyAuthorSymbol, getFirst);
        if (empty == false) return Optional.empty();

        // 1차 : 저자 초성에 대해 이진탐색
        Character myKey = separatedKeyAuthorSymbol.getAuthorInitialConsonant();
        Character myMid = null;
        boolean keyIsKorean;
        boolean midIsKorean;
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
            } else {
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
            throw new EmptyStackException(); // 값이 없는 건 DB에 접근하지 못했을 때 뿐임. 심각한 에러라는 것.
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
            throw new EmptyStackException();
        }
        Character setBookInit = temp2.get(midIndex).getBookInitialConsonant();

        // 최종적으로 결정된 조각들을 전부 조합. 한정된 리스트 내의 값들을 조합했기에, 예외만 터지지 않으면 반드시 일치하는 row가 존재하게 됨.
        String answer = setAuthorInit + String.valueOf(setNum) + setBookInit;

        // body에 채워넣을 객체값 만들기
        ColumnAddressResponseDto result = buildBookshelfAuthorSymbolToColumnAddressResponseDto(foundAuthorSymbols, answer);
        if (result == null)
            return Optional.empty();
        else
            return Optional.of(result);
    }

    private static boolean checkExceptionCase(SeparatedAuthorSymbolDto myKey, SeparatedAuthorSymbolDto getFirst) {
        boolean firstIsKorean;
        boolean keyIsKorean;
        
        // 1차 비교
        if('A' <= myKey.getAuthorInitialConsonant() && myKey.getAuthorInitialConsonant() <= 'z') keyIsKorean = false;
        else keyIsKorean = true;
        if('A' <= getFirst.getAuthorInitialConsonant() && getFirst.getAuthorInitialConsonant() <= 'z') firstIsKorean = false;
        else firstIsKorean = true;
        if(keyIsKorean && !firstIsKorean) {  // key < first
            return false;  // 무조건 예외임
        } else if (!keyIsKorean && firstIsKorean) {  // key > first
            return true;  // 무조건 예외 아님
        } else {
            if(myKey.getAuthorInitialConsonant() < getFirst.getAuthorInitialConsonant()) {  // key < first
                return false;
            } else if (myKey.getAuthorInitialConsonant() > getFirst.getAuthorInitialConsonant()) {
                return true;
            } else {
                // 아무것도 하지 않고 넘김
            }
        }
        
        // 2차 비교
        if(myKey.getNumber() < getFirst.getNumber()) {  // key < first
            return false;
        } else if(myKey.getNumber() > getFirst.getNumber()) {
            return true;
        } else {
            // 아무것도 하지 않고 넘김
        }

        // 3차 비교
        if('A' <= myKey.getBookInitialConsonant() && myKey.getBookInitialConsonant() <= 'z') keyIsKorean = false;
        else keyIsKorean = true;
        if('A' <= getFirst.getBookInitialConsonant() && getFirst.getBookInitialConsonant() <= 'z') firstIsKorean = false;
        else firstIsKorean = true;
        if(keyIsKorean && !firstIsKorean) {  // key < first
            return false;
        } else if (!keyIsKorean && firstIsKorean) {  // key > first
            // 아무것도 하지 않고 넘김. 정반대인 대소관계를 처리하기 위해서는 이 else if 문이 기능은 없어도 반드시 있어야 함.
        } else {
            if(myKey.getBookInitialConsonant() < getFirst.getBookInitialConsonant()) {  // key < first
                return false;
            }
        }

        return true;
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

    private ColumnAddressResponseDto catchExceptionCase(BigDecimal classificationNumber) {
        Bookshelf ans = bookshelfRepository.findTopClassificationNumberLessThanOrderByDesc(classificationNumber);
        return ColumnAddressResponseDto.builder()
                .category(ans.getColumnAddress().getCategory())
                .bookshelfNum(ans.getColumnAddress().getBookshelfNum())
                .columnNum(ans.getColumnAddress().getColumnNum())
                .build();
    }
}
