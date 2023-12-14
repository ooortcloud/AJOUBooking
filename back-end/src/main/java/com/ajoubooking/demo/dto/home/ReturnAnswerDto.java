package com.ajoubooking.demo.dto.home;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReturnAnswerDto {
    String answer;
    ColumnAddressResponseDto exceptionAnswer;
}
