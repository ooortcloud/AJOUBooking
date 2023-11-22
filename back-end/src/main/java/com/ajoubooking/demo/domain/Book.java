package com.ajoubooking.demo.domain;

import com.ajoubooking.demo.domain.embed.CallNumber;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Book {  // 도서관 DB 연동 전까지만 사용될 임시 테이블

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String book_name;

    @Embedded
    private CallNumber call_number;
}
