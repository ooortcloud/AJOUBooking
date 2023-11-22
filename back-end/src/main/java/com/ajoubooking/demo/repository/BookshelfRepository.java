package com.ajoubooking.demo.repository;

import com.ajoubooking.demo.domain.Bookshelf;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookshelfRepository extends JpaRepository<Bookshelf, Long> {

}
