package com.ajoubooking.demo.dto.admin;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter 
@Setter  // @ModelAttribute를 쓰기 위해서는 @Setter가 필요함
public class AdminDto {
    private String admin_id;
    private String pw;
}
