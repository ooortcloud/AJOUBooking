package com.ajoubooking.demo.bookshelf;

import com.ajoubooking.demo.dto.home.CallNumberDto;
import com.ajoubooking.demo.dto.home.ColumnAddressDto;
import com.ajoubooking.demo.service.HomeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest  // 통합 테스트
@Transactional
public class HomeServiceTest {
    private final HomeService homeService;

    @Autowired  // Jupitor5 가 의존성 주입이 필요한 생성자를 인지하도록 하여, Spring 컨테이너에 의존성 주입을 요청하도록 함
    public HomeServiceTest(HomeService homeService) {
        this.homeService = homeService;
    }

    @Test
    void testSeparateRequestCallNumber() {
        CallNumberDto callNumberDto = homeService.separateRequestCallNumber("005.8 B187hK한");
        CallNumberDto callNumberDto2 = CallNumberDto.builder()
                .classificationNumber(BigDecimal.valueOf(5.8))
                .authorSymbol("B187hK한")
                .build();

        assertThat(callNumberDto).usingRecursiveComparison().isEqualTo(callNumberDto2);
    }

    @Test
    void testSeparateRequestCallNumberException() {
        CallNumberDto callNumberDto = homeService.separateRequestCallNumber("일 005.8 B187hK한");
        CallNumberDto callNumberDto2 = CallNumberDto.builder()
                .classificationNumber(BigDecimal.valueOf(5.8))
                .authorSymbol("B187hK한")
                .build();

        assertThat(callNumberDto).usingRecursiveComparison().isEqualTo(callNumberDto2);
    }

    @Test
    @DisplayName("Service 이진탐색 전체 테스트")  // 값만 비교했을 때 성공
    void binarySearchForResponseTest() {
        Optional<ColumnAddressDto> result = homeService.binarySearchForResponse(CallNumberDto.builder()
                .classificationNumber(BigDecimal.valueOf(5.8))
                .authorSymbol("B187hK한")
                .build());

        ColumnAddressDto answer = ColumnAddressDto.builder()
                .category(0)
                .bookshelfNum(8)
                .columnNum(11)
                .build();

        assertThat(result.get()).usingRecursiveComparison().isEqualTo(answer);
    }

    /*
    @Test
    @DisplayName("저자 기호 분리 테스트(public 변경 후 진행할 것)")
    void binarySearchForAuthorTest() {
        Optional<ColumnAddressResponseDto> result = mainService.binarySearchForAuthor("B187hK한", new ArrayList<>());

        ColumnAddressResponseDto compare = Optional.ofNullable(ColumnAddressResponseDto.builder()
                                                                        .category(0)
                                                                        .bookshelfNum(8)
                                                                        .columnNum(11)
                                                                        .build());
        assertThat(result.get()).usingRecursiveComparison().isEqualTo(compare);
    }
     */

    /*
    @Test
    @DisplayName("저자기호 분리 테스트(public 변경 후 진행할 것)")  // 성공
    void separateAuthorSymbolTest() {
        SeparatedAuthorSymbolDto ans = mainService.separateAuthorSymbol("B187hK한");

        assertThat(ans).usingRecursiveComparison().isEqualTo(
                SeparatedAuthorSymbolDto.builder()
                        .authorInitialConsonant('B')
                        .number(187)
                        .bookInitialConsonant('h')
                        .build()
        );
    }
     */

}
