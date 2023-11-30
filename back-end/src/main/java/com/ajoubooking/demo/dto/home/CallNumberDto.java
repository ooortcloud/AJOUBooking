package com.ajoubooking.demo.dto.home;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class CallNumberDto {
    private BigDecimal classificationNumber;
    private String authorSymbol;
}
