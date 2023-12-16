package com.ajoubooking.demo.dto.home;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SeparatedAuthorSymbolDto {
    private Character authorInitialConsonant;  // D
    private Integer number;  // 262 (최대 3자리)
    private String bookInitialConsonant;  // 어
}
