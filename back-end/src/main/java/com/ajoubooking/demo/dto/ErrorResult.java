package com.ajoubooking.demo.dto;

import lombok.*;

@Builder
@Getter
public class ErrorResult {
    private String msg;  // 구체적인 오류
}
