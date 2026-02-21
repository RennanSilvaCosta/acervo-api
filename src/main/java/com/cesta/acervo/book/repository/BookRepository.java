package com.cesta.acervo.book.repository;

import com.cesta.acervo.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
