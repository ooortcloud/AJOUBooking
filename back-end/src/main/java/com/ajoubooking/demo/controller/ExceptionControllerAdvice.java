package com.ajoubooking.demo.controller;

import com.ajoubooking.demo.dto.ErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.InputMismatchException;

@RestControllerAdvice // 컨트롤러 전역에 대해 예외처리. HttpConverter를 사용하기 위해 Rest걸어줌.(JSON 반환을 위해서)
public class ExceptionControllerAdvice {

    // @ResponseStatus(HttpStatus.BAD_REQUEST)  // 상태번호를 지정하는 어노테이션. 하지만 ResponseEntity로 응답하면 유연한 상태번호 설정이 가능.
    @ExceptionHandler(InputMismatchException.class)  // 해당 예외에 대한 예외처리
    public ResponseEntity<ErrorResult> inputMismatchEx(InputMismatchException e) {
        return new ResponseEntity<>(ErrorResult.builder().msg(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ErrorResult> numberFormatEx(NumberFormatException e) {
        return new ResponseEntity<>(ErrorResult.builder().msg(e.getMessage()).build(), HttpStatus.BAD_REQUEST);
    }
}
