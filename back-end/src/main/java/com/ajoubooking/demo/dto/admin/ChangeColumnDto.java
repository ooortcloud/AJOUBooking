package com.ajoubooking.demo.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class ChangeColumnDto {
    @NotBlank
    private String inputCallNumber;
    private String previousCallNumber;
    private String nextCallNumber;
}
