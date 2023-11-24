package com.ajoubooking.demo.domain.embed;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.math.BigDecimal;

/*
    도서관 사이트에 표기되는 형태 : 005.8 B187hK한
    청구기호 순서 : ㄱㄴㄷ < abc < 123
    분류번호는 앞의 세자리, 뒤로 여섯 자리가 최대임.

    (주의) 자바의 순서 : 123 < ABC < abc < ㄱㄴㄷ
        >> 대소비교 역순임...
*/

@Embeddable
@Getter
public class CallNumber {
    @Column(name = "classification_number", precision = 9, scale = 6)  // decimal(precision, scale)로 생성됨
    private BigDecimal classificationNumber;  // 분류번호 : 808.8
                    /*
                        Float은 '근사값'인 부동 소수점 형태로 데이터를 저장한다.
                        이 때문에 DB에서 float 값을 다루면 제대로 조회가 이뤄지지 않는 문제가 발생한다.
                        반면 BigDecimal은 고정 소수점 형태로 데이터를 '정확하게' 저장한다.
                        그래서 정확히 의도한대로 CRUD를 할 수 있다.
                     */

    @Column(name = "author_symbol")
    private String authorSymbol;  // 저자기호 : 세14민

    // private String volumeSymbol;  // 권차기호 : v.44
    // private String copySymbol;  // 복본기호 : c.7
}