package com.ajoubooking.demo.dto;

import com.ajoubooking.demo.domain.embed.CallNumber;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CallNumberRequestDto {

    private CallNumber callNumber;
}
