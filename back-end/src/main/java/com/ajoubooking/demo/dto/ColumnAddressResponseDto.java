package com.ajoubooking.demo.dto;

import com.ajoubooking.demo.domain.embed.ColumnAddress;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
    출력 형식:
    "category" : "000",
    "bookshelf_num" : "8",
    "column_num" : "11"
 */

@Getter
// @NoArgsConstructor
                    /*
                        Spring이 클래스를 JSON으로 매핑할 때 Jackson 라이브러리의 ObjectMapper를 사용함.
                        ObjectMapper는 JSON 데이터 직렬화 또는 역직렬화를 위해 매핑할 때 기본 생성자를 이용함.
                        그래서 기본 생성자를 생성해줘야 함.
                    */
@Builder    /*
                생성자를 가독성 있게 이용할 수 있게 도와줌.
                @Builder를 사용하면 기본 생성자 생성을 보장함.
            */
public class ColumnAddressResponseDto {
    private String category;  // 000
    private Integer bookshelfNum;  // 1
    private Integer columnNum;  // 2
}