package com.ajoubooking.demo.service;

import com.ajoubooking.demo.domain.Bookshelf;
import com.ajoubooking.demo.dto.home.CallNumberDto;
import com.ajoubooking.demo.dto.home.ColumnAddressResponseDto;
import com.ajoubooking.demo.dto.home.ReturnAnswerDto;
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
        quickSortFromDbByAuthNum(foundRows, 1, foundRows.size() - 1);  // DB에서 넘어온 값을 저자기호 순서에 맞게 오름차순으로 재정렬

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
         * @return : 만약 key 저자기호가 가장 작은 저자기호보다 더 작으면 예외처리.
         */
        boolean exceptionCase = checkExceptionCase(separatedKeyAuthorSymbol, getFirst);
        if (exceptionCase == true) return Optional.empty();

        ReturnAnswerDto answer = compareAuthForBinarySearch(foundAuthorSymbols, separatedAuthorSymbols, separatedKeyAuthorSymbol);

        if(answer.getExceptionAnswer() != null) return Optional.of(answer.getExceptionAnswer());

        // body에 채워넣을 객체값 만들기
        ColumnAddressResponseDto result = buildBookshelfAuthorSymbolToColumnAddressResponseDto(foundAuthorSymbols, answer.getAnswer());
        if (result == null)
            return Optional.empty();
        else
            return Optional.of(result);
    }

    /**
     *  key < checkVal : false
     *  key >= checkVal : true
     *  
     * @param myKey
     * @param checkVal  : 기준 청구기호
     * @return
     */
    private static boolean checkExceptionCase(SeparatedAuthorSymbolDto myKey, SeparatedAuthorSymbolDto checkVal) {
        boolean firstIsKorean;
        boolean keyIsKorean;
        
        // 1차 비교
        if('A' <= myKey.getAuthorInitialConsonant() && myKey.getAuthorInitialConsonant() <= 'z') keyIsKorean = false;
        else keyIsKorean = true;
        if('A' <= checkVal.getAuthorInitialConsonant() && checkVal.getAuthorInitialConsonant() <= 'z') firstIsKorean = false;
        else firstIsKorean = true;
        if(keyIsKorean && !firstIsKorean) {  // key < first
            return true;  // 무조건 예외임
        } else if (!keyIsKorean && firstIsKorean) {  // key > first
            return false;  // 무조건 예외 아님
        } else {
            if(myKey.getAuthorInitialConsonant() < checkVal.getAuthorInitialConsonant()) {  // key < first
                return true;
            } else if (myKey.getAuthorInitialConsonant() > checkVal.getAuthorInitialConsonant()) {  // key > first
                return false;
            } else {
                // 아무것도 하지 않고 넘김
            }
        }
        
        // 2차 비교
        if(myKey.getNumber() < checkVal.getNumber()) {  // key < first
            return true;
        } else if(myKey.getNumber() > checkVal.getNumber()) {
            return false;
        } else {
            // 아무것도 하지 않고 넘김
        }

        // 3차 비교
        if('A' <= myKey.getBookInitialConsonant() && myKey.getBookInitialConsonant() <= 'z') keyIsKorean = false;
        else keyIsKorean = true;
        if('A' <= checkVal.getBookInitialConsonant() && checkVal.getBookInitialConsonant() <= 'z') firstIsKorean = false;
        else firstIsKorean = true;
        if(keyIsKorean && !firstIsKorean) {  // key < first
            return true;
        } else if (!keyIsKorean && firstIsKorean) {  // key > first
            // 아무것도 하지 않고 넘김. 정반대인 대소관계를 처리하기 위해서는 이 else if 문이 기능은 없어도 반드시 있어야 함.
        } else {
            if(myKey.getBookInitialConsonant() < checkVal.getBookInitialConsonant()) {  // key < first
                return true;
            }
        }

        return false;
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
        Bookshelf temp = bookshelfRepository.findTopClassificationNumberLessThanOrderByDesc(classificationNumber);
        List<Bookshelf> ans = bookshelfRepository.findByClassificationNumber(temp.getStartCallNumber().getClassificationNumber());
        // DB에서 가져온 값들을 저자기호 순서체계에 맞게 재정렬
        quickSortFromDbByAuthNum(ans, 1, ans.size() - 1);

        // 맨 끝에 존재하는 리스트 요소를 리턴하면 이 예외처리가 보장됨
        int end = ans.size()-1;
        return ColumnAddressResponseDto.builder()
                .category(ans.get(end).getColumnAddress().getCategory())
                .bookshelfNum(ans.get(end).getColumnAddress().getBookshelfNum())
                .columnNum(ans.get(end).getColumnAddress().getColumnNum())
                .build();
    }

    /**
     * 퀵정렬은 정렬된 리스트에 대해서 최악 O(n^2)의 시간복잡도를 갖지만, id에 의존한 정렬이 아니면 저자기호에서 순서가 반드시 보장되지 못하므로 퀵정렬을 사용함.
     * @param tempList  : Java에서 list와 같은 참조 타입은 call by 'reference' 형태로 처리된다.
     * @param low  : 리스트에서 pivot을 제외한 가장 낮은 인덱스 번호
     * @param high : 리스트에서 가장 높은 인덱스 번호
     */
    private void quickSortFromDbByAuthNum(List<Bookshelf> tempList, int low, int high) {
        // '부분 리스트'의 원소 개수가 '1개 이하'일 때 conquer 전략을 끝냄.
        if(low >= high) return;

        // pivot 기준으로 한쪽은 pivot보다 크고 반대쪽은 pivot보다 작은 두 '부분 리스트'를 만들어냄
        int pivot = partition(tempList, low, high);

        // 두 부분 리스트를 'pivot' 기준으로 잘라서 재귀정렬.
        quickSortFromDbByAuthNum(tempList, low, pivot - 1);
        quickSortFromDbByAuthNum(tempList, pivot + 1, high);
    }

    // 저자기호 순서를 기준으로 정렬해야 한다.
    private int partition(List<Bookshelf> tempList, int low, int high) {
        // pivot이 인덱스 0인 퀵정렬을 구현하였음
        SeparatedAuthorSymbolDto pivot = separateAuthorSymbol(tempList.get(0).getStartCallNumber().getAuthorSymbol());

        // low와 high 인덱스 번호가 서로 겹치거나 역전되면 반복문을 종료
        while(low >= high) {
            /**
             *  pivot > lowAuth 인 인덱스를 찾을 때까지 low 인덱스 증가
             *  도중에 low와 high가 역전되면 반복문을 종료
             */
            while(compareAuthForQuickSort(tempList, 0, low) == 1 && low < high) {
                low++;
            }
            /**
             *  pivot < highAuth인 인덱스를 찾을 때까지 high 인덱스 감소
             *  도중에 low와 high가 역전되면 반복문을 종료
             */
            while (compareAuthForQuickSort(tempList, 0, high) == -1 && low < high) {
                high--;
            }
            /**
             *  low 인덱스 원소와 high 인덱스 원소를 교환.
             *  low와 high가 겹쳐있더라도 스스로를 바꾸는 것이므로, 이 예외에 대한 분기문 처리 안 해도 됨. (매번 분기처리 하는 것이 비용이 더 큼)
             */
            swap(tempList, low, high);
        }

        // 역전 후 high의 인덱스를 반환하면 두 부분 리스트를 pivot의 크기 기준으로 나눌 수 있다.
        return high;
    }

    private void swap(List<Bookshelf> tempList, int low, int high) {
        Bookshelf temp = tempList.get(low);
        tempList.set(low, tempList.get(high));
        tempList.set(high, temp);
    }

    /**
     *  왼 < 오 : -1
     *  왼 = 오 : 0  << 근데 이 케이스가 존재할리는 없음. (중복이 없으니까)
     *  왼 > 오 : 1
     */
    private int compareAuthForQuickSort(List<Bookshelf> tempList, int left, int right) {
        SeparatedAuthorSymbolDto leftVal = separateAuthorSymbol(tempList.get(left).getStartCallNumber().getAuthorSymbol());
        SeparatedAuthorSymbolDto rightVal = separateAuthorSymbol(tempList.get(right).getStartCallNumber().getAuthorSymbol());
        boolean leftIsKorean;
        boolean rightIsKorean;

        // 1차 탐색 : 저자 초성
        Character leftValAuthorInitialConsonant = leftVal.getAuthorInitialConsonant();
        Character rightValAuthorInitialConsonant = rightVal.getAuthorInitialConsonant();

        if('A' <= leftValAuthorInitialConsonant && leftValAuthorInitialConsonant <= 'z') leftIsKorean = false;
        else leftIsKorean = true;
        if('A' <= rightValAuthorInitialConsonant && rightValAuthorInitialConsonant <= 'z') rightIsKorean = false;
        else rightIsKorean = true;

        if(leftIsKorean && !rightIsKorean) {  // left < right
            return -1;
        } else if (!leftIsKorean && rightIsKorean) {  // left > right
            return 1;
        } else {
            if(leftValAuthorInitialConsonant < rightValAuthorInitialConsonant) {
                return -1;
            } else if (leftValAuthorInitialConsonant > rightValAuthorInitialConsonant) {
                return 1;
            } else {
                // 아무것도 하지 않음
            }
        }
        
        // 2차 탐색 : 숫자 탐색
        if(leftVal.getNumber() < rightVal.getNumber())
            return -1;
        else if(leftVal.getNumber() > rightVal.getNumber())
            return 1;
        else{
            // 아무것도 하지 않음
        }
        
        // 3차 탐색 : 도서 초성
        Character leftValBookInitialConsonant = leftVal.getBookInitialConsonant();
        Character rightValBookInitialConsonant = rightVal.getBookInitialConsonant();

        if('A' <= leftValBookInitialConsonant && leftValBookInitialConsonant <= 'z') leftIsKorean = false;
        else leftIsKorean = true;
        if('A' <= rightValBookInitialConsonant && rightValBookInitialConsonant <= 'z') rightIsKorean = false;
        else rightIsKorean = true;

        if(leftIsKorean && !rightIsKorean) {  // left < right
            return -1;
        } else if (!leftIsKorean && rightIsKorean) {  // left > right
            return 1;
        } else {
            if(leftValBookInitialConsonant < rightValBookInitialConsonant) {
                return -1;
            } else if (leftValBookInitialConsonant > rightValBookInitialConsonant) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /**
     *  크기 비교를 하면서 문자열을 조합함.
     *  주어진 리스트 내에서 조합하는 것이기 때문에, 존재하지 않는 쌍이 발생할 예외는 발생하지 않는다.
     */
    private ReturnAnswerDto compareAuthForBinarySearch(List<Bookshelf> foundAuthorSymbols, List<SeparatedAuthorSymbolDto> separatedAuthorSymbols, SeparatedAuthorSymbolDto separatedKeyAuthorSymbol) {
        int lowIndex = 0;
        int highIndex = foundAuthorSymbols.size() - 1;
        Integer midIndex = null;

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
                    // DB에 column의 첫 청구기호를 저장해두기에, 이 케이스에서는 반드시 해당 column보다 작음이 보장되므로 예외가 발생하지 않음.
                    highIndex = midIndex - 1;
                } else if (myKey > myMid) {
                    /**
                     *  여기서는 현재 청구기호보다는 크지만 다음 청구기호보다는 작은, column 사이에 낑기는 케이스가 생길 수 있어 예외가 발생할 수 있음.
                     *  여기서 mid의 다음 청구기호와 비교했을 때, 해당 청구기호보다 작으면 stop하고 mid 청구기호를 리턴쳐야 함.
                     *  어차피 리스트를 갖고 있으니까 리스트에서 다음 청구기호와 비교하면 됨.
                     *  리스트 마지막 요소의 경우에는 애초에 DB에서 긁어올 때 분류번호 선에서 일차적으로 컷했기 때문에 해당 예외가 발생하지 않으므로 무시.
                     *
                     *  이 예외처리 때문에 중복 코드를 함축시키고 싶어도 조금씩 달라서 빼기가 난감함...
                     */
                    if(midIndex < separatedAuthorSymbols.size() - 1) {
                        boolean nextOfMidIsKorean;
                        Character nextOfMid = separatedAuthorSymbols.get(midIndex+1).getAuthorInitialConsonant();
                        if('A'<=nextOfMid && nextOfMid <= 'z') nextOfMidIsKorean=false;
                        else nextOfMidIsKorean=true;

                        if(myKey < separatedAuthorSymbols.get(midIndex+1).getAuthorInitialConsonant() || (!nextOfMidIsKorean && keyIsKorean)) {  // key < nextOfMid
                            SeparatedAuthorSymbolDto temp = separatedAuthorSymbols.get(midIndex);
                            String tempToString = temp.getAuthorInitialConsonant() + temp.getNumber().toString() + temp.getBookInitialConsonant();
                            ColumnAddressResponseDto ans = buildBookshelfAuthorSymbolToColumnAddressResponseDto(foundAuthorSymbols, tempToString);
                            return ReturnAnswerDto.builder()
                                    .answer(null)
                                    .exceptionAnswer(ans)
                                    .build();
                        }
                    }

                    lowIndex = midIndex + 1;
                } else {
                    /**
                     *  만약 저자 초성이 같은 경우에, 그 다음 숫자가 작아서 실제 키의 위치가 현재 column보다 앞에 있는 예외가 발생할 수 있다.
                     *  따라서 현재 저자기호의 숫자와 key의 저자기호의 숫자를 서로 비교했을 때 key의 숫자가 더 작다면,
                     *  key는 반드시 현재 column의 바로 앞에 위치하는 것이 보장된다.
                     *
                     *  해당 예외가 발생하지 않았다면, key의 위치는 반드시 주어진 저자초성과 동일한 것을 갖는 기준 청구기호 column 내에 존재한다.
                     *  
                     *  2차 분기문에서 처리해도 되지만, 여기서 처리하는 것이 로직 재활용에 유리함.
                     */
                    boolean exceptionCase = checkExceptionCase(separatedKeyAuthorSymbol, separatedAuthorSymbols.get(midIndex));
                    if(exceptionCase == true) {
                        SeparatedAuthorSymbolDto temp = separatedAuthorSymbols.get(midIndex - 1);
                        String tempToString = temp.getAuthorInitialConsonant() + temp.getNumber().toString() + temp.getBookInitialConsonant();
                        ColumnAddressResponseDto ans = buildBookshelfAuthorSymbolToColumnAddressResponseDto(foundAuthorSymbols, tempToString);
                        return ReturnAnswerDto.builder()
                                .answer(null)
                                .exceptionAnswer(ans)
                                .build();
                    }
                    break;
                }
            }
        }
        Character setAuthorInit = myMid;

        // 2차 : 숫자에 대해 이진탐색
        // 예상되는 리스트 길이가 매우 짧을 것으로 예상되므로 순차탐색을 진행함
        List<SeparatedAuthorSymbolDto> temp = new ArrayList<>();
        for (SeparatedAuthorSymbolDto authorSymbol : separatedAuthorSymbols) {
            if(authorSymbol.getAuthorInitialConsonant().compareTo(setAuthorInit) == 0)
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
                } else if (myKey2 > myMid2) {
                    if(midIndex < temp.size() - 1) {
                        // 여기서도 column 사이에 낑기는 케이스 예외처리.
                        if(myKey2 < temp.get(midIndex+1).getNumber()) {
                            SeparatedAuthorSymbolDto t = temp.get(midIndex);
                            String tempToString = t.getAuthorInitialConsonant() + t.getNumber().toString() + t.getBookInitialConsonant();
                            ColumnAddressResponseDto ans = buildBookshelfAuthorSymbolToColumnAddressResponseDto(foundAuthorSymbols, tempToString);
                            return ReturnAnswerDto.builder()
                                    .answer(null)
                                    .exceptionAnswer(ans)
                                    .build();
                        }
                    }

                    lowIndex = midIndex + 1;
                } else {
                    /**
                     *  여기서도 만약 숫자가 같은 경우, 그 다음 도서초성이 작아서 현재 column보다 앞에 있게 되는 예외를 처리해야 한다.
                     *  해당 예외가 발생하지 않았다면, key의 위치는 반드시 주어진 저자초성과 숫자를 갖는 기준 청구기호가 있는 column 내에 존재한다.
                     */
                    boolean exceptionCase = checkExceptionCase(separatedKeyAuthorSymbol, temp.get(midIndex));
                    if(exceptionCase == true) {
                        // 일단 기존 리스트에서 인덱스 위치를 다시 찾아야 함
                        SeparatedAuthorSymbolDto needToFind = temp.get(midIndex);
                        int i = 0;
                        for (SeparatedAuthorSymbolDto x : separatedAuthorSymbols) {
                            if(needToFind.equals(x)) {
                                break;
                            }
                            i++;
                        }

                        // 기존 리스트에서 현재 column보다 하나 앞선 column이 정답임
                        SeparatedAuthorSymbolDto authorSymbolDto = separatedAuthorSymbols.get(i - 1);
                        String tempToString = authorSymbolDto.getAuthorInitialConsonant() + authorSymbolDto.getNumber().toString() + authorSymbolDto.getBookInitialConsonant();
                        ColumnAddressResponseDto ans = buildBookshelfAuthorSymbolToColumnAddressResponseDto(foundAuthorSymbols, tempToString);
                        return ReturnAnswerDto.builder()
                                .answer(null)
                                .exceptionAnswer(ans)
                                .build();
                    }

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
                        // 여기서도 같은 이치로 예외처리가 들어가야 한다.
                        if(midIndex < temp2.size() - 1) {
                            boolean nextOfMidIsKorean;
                            Character nextOfMid = separatedAuthorSymbols.get(midIndex+1).getBookInitialConsonant();
                            if('A'<=nextOfMid && nextOfMid <= 'z') nextOfMidIsKorean=false;
                            else nextOfMidIsKorean=true;

                            if(myKey < temp2.get(midIndex+1).getBookInitialConsonant() || (!nextOfMidIsKorean && keyIsKorean)) {  // key < nextOfMid
                                SeparatedAuthorSymbolDto t = temp2.get(midIndex);
                                String tempToString = t.getAuthorInitialConsonant() + t.getNumber().toString() + t.getBookInitialConsonant();
                                ColumnAddressResponseDto ans = buildBookshelfAuthorSymbolToColumnAddressResponseDto(foundAuthorSymbols, tempToString);
                                return ReturnAnswerDto.builder()
                                        .answer(null)
                                        .exceptionAnswer(ans)
                                        .build();
                            }
                        }

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
        return ReturnAnswerDto.builder()
                .answer(answer)
                .exceptionAnswer(null)
                .build();
    }
}
