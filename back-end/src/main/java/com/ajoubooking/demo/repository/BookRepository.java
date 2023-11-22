package com.ajoubooking.demo.repository;

import com.ajoubooking.demo.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {



}
