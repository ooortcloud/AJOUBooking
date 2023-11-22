package com.ajoubooking.demo.domain.embed;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;

@Embeddable  // @Embedded 타입에 적용될 클래스를 생성
@Getter
public class ColumnAddress {
    private String category;  // 000
    private Integer bookshelf_num;  // 1
    private Integer column_num;  // 2
}
