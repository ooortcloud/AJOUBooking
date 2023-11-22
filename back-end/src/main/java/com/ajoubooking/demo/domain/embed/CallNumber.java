package com.ajoubooking.demo.domain.embed;

import jakarta.persistence.Embeddable;
import lombok.Getter;

/*
    도서관 사이트에 표기되는 형태 : 005.8 B187hK한
*/

@Embeddable
@Getter
public class CallNumber {
    private Float classification_number;  // 808.8
    private String author_symbol;  // 세14민
    // private String volume_symbol;  // v.44
    // private String copy_symbol;  // c.7
}