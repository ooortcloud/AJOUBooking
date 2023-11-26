package com.ajoubooking.demo.dto.home;

import lombok.Getter;
import lombok.NoArgsConstructor;

/*
    입력 형식 :
    "callNumber" : "005.8 B187hK한"
 */
@Getter
@NoArgsConstructor
public class StringRequestDto {
    private String callNumber;
}
