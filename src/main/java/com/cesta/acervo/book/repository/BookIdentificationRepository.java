package com.cesta.acervo.book.repository;

import com.cesta.acervo.book.entity.BookIdentification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookIdentificationRepository extends JpaRepository<BookIdentification, Long> {
}
