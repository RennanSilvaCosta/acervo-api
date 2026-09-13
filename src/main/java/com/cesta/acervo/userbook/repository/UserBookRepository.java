package com.cesta.acervo.userbook.repository;

import com.cesta.acervo.userbook.entity.UserBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserBookRepository extends JpaRepository<UserBook, Long> {

    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    @Query("""
            SELECT ub FROM TB_USER_BOOK ub
            JOIN FETCH ub.book
            JOIN FETCH ub.user
            WHERE ub.user.id = :userId
              AND ub.book.id = :bookId
            """)
    Optional<UserBook> findByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Query("""
            SELECT ub FROM TB_USER_BOOK ub
            JOIN FETCH ub.book
            WHERE ub.user.id = :userId
            ORDER BY ub.addedAt DESC
            """)
    List<UserBook> findAllByUserIdWithBook(@Param("userId") Long userId);
}
