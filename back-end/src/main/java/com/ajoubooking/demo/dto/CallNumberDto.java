package com.ajoubooking.demo.dto;

import com.ajoubooking.demo.domain.embed.CallNumber;
import lombok.Builder;
import lombok.Getter;

@Builder  // 가독성 있게 생성자를 이용할 수 있게 도와줌
@Getter
public class CallNumberDto {
    private CallNumber callNumber;
}
