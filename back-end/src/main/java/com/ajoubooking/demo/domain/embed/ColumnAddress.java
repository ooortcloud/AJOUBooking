package com.ajoubooking.demo.domain.embed;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable  // @Embedded 타입에 적용될 클래스를 생성
@Getter
public class ColumnAddress {
    private String category;  // 000

    @Column(name =  "bookshelf_num")  // 해당 column을 지정한 이름으로 DB에 저장
    private Integer bookshelfNum;  // 1
    @Column(name =  "column_num")
    private Integer columnNum;  // 2
}
