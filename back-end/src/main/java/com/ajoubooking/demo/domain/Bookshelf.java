package com.ajoubooking.demo.domain;

import com.ajoubooking.demo.domain.embed.ColumnAddress;
import com.ajoubooking.demo.domain.embed.CallNumber;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity  // 현 도메인 클래스와 DB 테이블을 매핑
@NoArgsConstructor  // @Entity 사용 시 기본 생성자 필수
@Getter  // read만 가능하게 함
public class Bookshelf {

    @Id  // 기본키로 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본값을 AUTO_INCREMENT로 설정
    private Long id;

    @Embedded  // 임베디드 타입 선언
    private CallNumber startCallNumber;

    @Embedded  // 임베디드 타입 선언
    private ColumnAddress columnAddress;
}