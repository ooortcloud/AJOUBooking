package com.ajoubooking.demo.domain.embed;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

/*
    도서관 사이트에 표기되는 형태 : 005.8 B187hK한
    청구기호 순서 : ㄱㄴㄷ > abc > 123
*/

@Embeddable
@Getter
public class CallNumber {
    @Column(name = "classification_number")
    private Float classificationNumber;  // 분류번호 : 808.8

    @Column(name = "author_symbol")
    private String authorSymbol;  // 저자기호 : 세14민
    // private String volumeSymbol;  // 권차기호 : v.44
    // private String copySymbol;  // 복본기호 : c.7
}