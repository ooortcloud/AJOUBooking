package com.ajoubooking.demo.repository.bookshelf;

import com.ajoubooking.demo.domain.Bookshelf;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class BookshelfRepository {
    private final BookshelfDataRepository bookshelfDataRepository;
    private final BookshelfQueryRepository bookshelfQueryRepository;

    public BookshelfRepository(BookshelfDataRepository bookshelfDataRepository, BookshelfQueryRepository bookshelfQueryRepository) {
        this.bookshelfDataRepository = bookshelfDataRepository;
        this.bookshelfQueryRepository = bookshelfQueryRepository;
    }

    public List<Bookshelf> findByClassificationNumber(BigDecimal classificationNumber) {
        return bookshelfDataRepository.findByStartCallNumberClassificationNumber(classificationNumber);
    }

    // 아마 안쓸듯.
    public List<Bookshelf> findTopTwoLessThanEqualClassificationNumber(BigDecimal classificationNumber) {
        return bookshelfQueryRepository.findTopTwoLessThanEqualClassificationNumber(classificationNumber);
    }

    public BigDecimal findTopClassificationNumberLessThanEqualOrderByDesc(BigDecimal classificationNum) {
        Bookshelf temp = bookshelfDataRepository.findFirstByStartCallNumberClassificationNumberLessThanEqualOrderByStartCallNumberClassificationNumberDesc(classificationNum);
        return temp.getStartCallNumber().getClassificationNumber();
    }

    public Bookshelf findTopClassificationNumberLessThanOrderByDesc(BigDecimal classificationNum) {
        return bookshelfDataRepository.findFirstByStartCallNumberClassificationNumberLessThanOrderByStartCallNumberClassificationNumberDesc(classificationNum);
    }
}
