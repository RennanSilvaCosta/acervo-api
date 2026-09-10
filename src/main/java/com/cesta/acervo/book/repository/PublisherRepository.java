package com.cesta.acervo.book.repository;

import com.cesta.acervo.book.entity.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublisherRepository extends JpaRepository<Publisher, Long> {
}
