package com.cesta.acervo.book.repository;

import com.cesta.acervo.book.entity.Cover;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoverRepository extends JpaRepository<Cover, Long> {
}
