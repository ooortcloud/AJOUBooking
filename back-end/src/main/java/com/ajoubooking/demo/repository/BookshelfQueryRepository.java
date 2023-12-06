package com.ajoubooking.demo.repository;

import com.ajoubooking.demo.domain.QBookshelf;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.ajoubooking.demo.domain.QBookshelf.bookshelf;

@Repository
@Transactional
public class BookshelfQueryRepository {
    private JPAQueryFactory queryFactory;

    public BookshelfQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    public List<BigDecimal> findTopTwoLessThanClassificationNumber(BigDecimal classificationNum) {
        return queryFactory.selectFrom(bookshelf)
                .
    }

}
