package com.ajoubooking.demo.repository.bookshelf;

import com.ajoubooking.demo.domain.Bookshelf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BookshelfDataRepository extends JpaRepository<Bookshelf, Long> {  // JpaRepository<엔티티, 엔티티 기본키 속성> 인터페이스 상속

    /*  입력된 값보다 작거나 같은 값 중 DB 상에서 가장 큰 값을 리턴.
    Bookshelf findFirstByStartCallNumberClassificationNumberLessThanEqualOrderByStartCallNumberClassificationNumberDesc(BigDecimal classificationNumber);
    */

    // "Entity 필드명 + Embedded 필드명" 순차적으로 선언
    // 필드명을 Camel 스타일로 작성 가능
    @Query(value = "select n from Bookshelf n where n.startCallNumber.classificationNumber = :v")  // 직접 JPQL로 쿼리문 생성
    List<Bookshelf> findByStartCallNumberClassificationNumber(@Param("v") BigDecimal classificationNumber);  // @Param은 JPQL 파라미터 적용 시 사용
    /*
        1. 입력된 분류번호와 일치하는 모든 row들을 리턴.
     */
}
