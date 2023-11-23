package com.ajoubooking.demo.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CallNumberDto {
    private Float classificationNumber;
    private String authorSymbol;
}
