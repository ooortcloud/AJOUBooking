package com.ajoubooking.demo.dto.home;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
    입력 형식 :
    "callNumber" : "005.8 B187hK한"
 */
@Getter
@NoArgsConstructor
@Setter  // @RequestBody를 사용한 JSON 변환에 필요
public class StringRequestDto {
    private String callNumber;
}
