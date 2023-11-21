package com.ajoubooking.demo.domain;

import com.ajoubooking.demo.domain.embed.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity  // 현 도메인 클래스와 DB 테이블을 매핑
@NoArgsConstructor  // @Entity 사용 시 기본 생성자 필수
@Getter  // 이 인스턴스는 read만 가능케함
public class BookShelf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String start_call_number;
    private String end_call_number;

    @Embedded  // 임베디드 타입 선언
    private Address address;
}