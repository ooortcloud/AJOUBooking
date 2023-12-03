package com.ajoubooking.demo.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class ChangePwDto {
    @NotBlank(message = "공백은 허용되지 않습니다.")
    private String inputA;
    @NotBlank(message = "공백은 허용되지 않습니다.")
    private String inputB;
}
