package com.cesta.acervo.book.repository;

import com.cesta.acervo.book.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
