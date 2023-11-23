package com.ajoubooking.demo;

import com.ajoubooking.demo.dto.CallNumberDto;
import com.ajoubooking.demo.service.MainService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest  // 통합 테스트
@Transactional
public class MainServiceTest {
    private final MainService mainService;

    @Autowired  // Jupitor5 가 의존성 주입이 필요한 생성자를 인지하도록 하여, Spring 컨테이너에 의존성 주입을 요청하도록 함
    public MainServiceTest(MainService mainService) {
        this.mainService = mainService;
    }

    @Test
    void testSeparateRequestCallNumber() {
        CallNumberDto callNumberDto = mainService.separateRequestCallNumber("005.8 B187hK한");
        CallNumberDto callNumberDto2 = CallNumberDto.builder()
                .classificationNumber(5.8f)
                .authorSymbol("B187hK한")
                .build();

        assertThat(callNumberDto).usingRecursiveComparison().isEqualTo(callNumberDto2);
    }

    @Test
    void testSeparateRequestCallNumberException() {
        CallNumberDto callNumberDto = mainService.separateRequestCallNumber("일 005.8 B187hK한");
        CallNumberDto callNumberDto2 = CallNumberDto.builder()
                .classificationNumber(5.8f)
                .authorSymbol("B187hK한")
                .build();

        assertThat(callNumberDto).usingRecursiveComparison().isEqualTo(callNumberDto2);
    }
}
