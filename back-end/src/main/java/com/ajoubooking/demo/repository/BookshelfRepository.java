package com.ajoubooking.demo.repository;

import com.ajoubooking.demo.domain.Bookshelf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookshelfRepository extends JpaRepository<Bookshelf, Long> {

    // "Entity 필드명 + Embedded 필드명" 순차적으로 선언
    // Camel 스타일로 작성 가능
    List<Bookshelf> findAllByStartCallNumberClassificationNumber(Float classificationNumber);
}
