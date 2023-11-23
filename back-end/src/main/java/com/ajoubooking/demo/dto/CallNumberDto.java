package com.ajoubooking.demo.dto;

import lombok.Builder;
import lombok.Getter;

@Builder  // 생성자를 가독성 있게 이용할 수 있게 도와줌
@Getter
public class CallNumberDto {
    private Float classificationNumber;
    private String authorSymbol;
}
