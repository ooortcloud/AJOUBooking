package com.ajoubooking.demo.repository.bookshelf;

import com.ajoubooking.demo.domain.Bookshelf;
import com.ajoubooking.demo.dto.home.CallNumberDto;
import com.ajoubooking.demo.dto.home.ColumnAddressResponseDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

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
                .orderBy(bookshelf.startCallNumber.classificationNumber.desc())  // 내림차순 조회
                .limit(2)
                .fetch();  // 리스트 조회
    }

    /* 생각해보니 기존 코드 재활용하면 될 거 같아서 안 만듦.
    public Bookshelf findTopFirstLessThanClassificationNumberDesc(BigDecimal classificationNum) {
        return queryFactory.select(bookshelf)
                .from(bookshelf)
                .where()
    }

     */

    public Bookshelf findBookshelfByCallNumber(CallNumberDto dto) {
        Bookshelf ans = queryFactory.selectFrom(bookshelf)
                .where(bookshelf.startCallNumber.classificationNumber.eq(dto.getClassificationNumber())
                        .and(bookshelf.startCallNumber.authorSymbol.eq(dto.getAuthorSymbol())))
                .fetchOne();// 단건 조회
        return ans;
    }

    public Bookshelf findBookshelfByColumnAddress(ColumnAddressResponseDto dto) {
        return queryFactory.selectFrom(bookshelf)
                .where(bookshelf.columnAddress.category.eq(dto.getCategory())
                        .and(bookshelf.columnAddress.bookshelfNum.eq(dto.getBookshelfNum()))
                        .and(bookshelf.columnAddress.columnNum.eq(dto.getColumnNum())))
                .fetchOne();
    }

    public Bookshelf findFirstBookshelfByColumnAddressOrderByColumnNumDesc(ColumnAddressResponseDto dto) {
        return queryFactory.selectFrom(bookshelf)
                .where(bookshelf.columnAddress.category.eq(dto.getCategory())
                        .and(bookshelf.columnAddress.bookshelfNum.lt(dto.getBookshelfNum())))
                .orderBy(bookshelf.columnAddress.columnNum.desc())
                .orderBy(bookshelf.columnAddress.bookshelfNum.desc())
                .orderBy(bookshelf.columnAddress.category.desc())
                .limit(1)
                .fetchOne();
    }

    public Bookshelf findFirstBookshelfByColumnAddressOrderByColumnNumDescForException(ColumnAddressResponseDto dto) {
        return queryFactory.selectFrom(bookshelf)
                .where(bookshelf.columnAddress.category.lt(dto.getCategory()))
                .orderBy(bookshelf.columnAddress.columnNum.desc())
                .orderBy(bookshelf.columnAddress.bookshelfNum.desc())
                .orderBy(bookshelf.columnAddress.category.desc())
                .limit(1)
                .fetchOne();
    }
}
