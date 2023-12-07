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

    public List<Bookshelf> findTopTwoLessThanEqualClassificationNumber(BigDecimal classificationNumber) {
        return bookshelfQueryRepository.findTopTwoLessThanEqualClassificationNumber(classificationNumber);
    }
}
