package com.ajoubooking.demo.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SeparatedAuthorSymbolDto {
    private String authorInitialConsonant;  // D
    private Integer number;  // 262 (최대 3자리)
    private String bookInitialConsonant;  // 어
}
