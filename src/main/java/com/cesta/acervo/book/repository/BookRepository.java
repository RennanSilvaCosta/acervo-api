package com.cesta.acervo.book.repository;

import com.cesta.acervo.book.entity.Book;
import com.cesta.acervo.book.enums.IdentifyTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("""
            SELECT DISTINCT b FROM TB_BOOK b
            INNER JOIN b.bookIdentifications iden
            WHERE iden.identifyType IN :types
              AND iden.identify = :isbn
            """)
    Optional<Book> findByIsbnCode(@Param("isbn") String isbn, @Param("types") List<IdentifyTypeEnum> types);


}
