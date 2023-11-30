package com.ajoubooking.demo.controller;

import com.ajoubooking.demo.dto.home.CallNumberDto;
import com.ajoubooking.demo.dto.home.ColumnAddressResponseDto;
import com.ajoubooking.demo.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.Optional;

// @CrossOrigin(origins = "http://localhost:3000/*")  // CORS 해결 어노테이션
@RestController  // 자바 객체를 HttpResponse의 본문 내용과 매핑해줌
class HomeController {

    @Autowired  // Spring이 해당 Bean 클래스에 대해서 자동 DI를 해준다.
    private MainService mainService;

    // POST로 값을 입력받으면 DB 데이터를 바탕으로 로직으로 처리해서 Front서버에 반환
    @GetMapping("")
    public ResponseEntity<ColumnAddressResponseDto> response(@RequestParam(value = "callNumber") String callNumber) {
        Optional<ColumnAddressResponseDto> responseDto;
        try {
            CallNumberDto requestedCallNumber = mainService.separateRequestCallNumber(callNumber);
            responseDto = mainService.binarySearchForResponse(requestedCallNumber);
        } catch (Exception e) {
            System.out.println(LocalDate.now() + " >> " +  e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(responseDto.isPresent())
            return ResponseEntity.ok(responseDto.get());
        else{
            System.out.println("내부 이진 탐색 연산이 잘못됨...");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/health")
    public ResponseEntity sendOk() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
