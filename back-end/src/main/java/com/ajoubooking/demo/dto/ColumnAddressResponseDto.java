package com.ajoubooking.demo.dto;

import com.ajoubooking.demo.domain.embed.ColumnAddress;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
    출력 형식:
    "category" : "000",
    "bookshelf_num" : "8",
    "column_num" : "11"
 */

@Getter
@NoArgsConstructor  /*
                        Spring이 클래스를 JSON으로 매핑할 때 Jackson 라이브러리의 ObjectMapper를 사용함.
                        ObjectMapper는 JSON 데이터 직렬화 또는 역직렬화를 위해 매핑할 때 기본 생성자를 이용함.
                        그래서 기본 생성자를 생성해줘야 함.
                    */
public class ColumnAddressResponseDto {
    private ColumnAddress address;
}