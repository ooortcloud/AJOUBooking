package com.ajoubooking.demo.repository.bookshelf;

import com.ajoubooking.demo.domain.Bookshelf;
import com.ajoubooking.demo.domain.QBookshelf;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.ajoubooking.demo.domain.QBookshelf.bookshelf;

@Repository
public class BookshelfQueryRepository {
    private JPAQueryFactory queryFactory;

    public BookshelfQueryRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }


    public List<Bookshelf> findTopTwoLessThanEqualClassificationNumber(BigDecimal classificationNum) {
        return queryFactory.selectDistinct(bookshelf)
                .from(bookshelf)
                .where(bookshelf.startCallNumber.classificationNumber.loe(classificationNum))
                .orderBy(bookshelf.startCallNumber.classificationNumber.desc())
                .limit(2)
                .fetch();
    }

    /* 생각해보니 기존 코드 재활용하면 될 거 같아서 안 만듦.
    public Bookshelf findTopFirstLessThanClassificationNumberDesc(BigDecimal classificationNum) {
        return queryFactory.select(bookshelf)
                .from(bookshelf)
                .where()
    }

     */
}
