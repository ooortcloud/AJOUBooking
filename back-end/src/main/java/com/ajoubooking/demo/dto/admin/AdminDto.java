package com.ajoubooking.demo.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter 
@Setter  // @ModelAttribute를 쓰기 위해서는 @Setter가 필요함
public class AdminDto {
    @NotBlank(message = "공백은 입력할 수 없습니다.")  // 필드값에 대해 공백 또는 빈칸만으로 데이터가 들어오지 못하도록 설정(Bean Validation)
    private String adminId;
    @NotBlank(message = "공백은 입력할 수 없습니다.")
    private String pw;
}
