package com.cesta.acervo.userbook.entity;

import com.cesta.acervo.book.entity.Book;
import com.cesta.acervo.userbook.enums.StatusReading;
import com.cesta.acervo.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "TB_USER_BOOK")
@Table(
        name = "TB_USER_BOOK",
        uniqueConstraints = @UniqueConstraint(name = "UK_USER_BOOK", columnNames = {"USER_ID", "BOOK_ID"})
)
public class UserBook {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "USER_BOOK_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BOOK_ID", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_BOOK_STATUS_READING", nullable = false)
    private StatusReading statusReading;

    @Column(name = "USER_BOOK_ADDED_AT", nullable = false)
    private Instant addedAt;

    @Column(name = "USER_BOOK_STARTED_AT")
    private LocalDate startedAt;

    @Column(name = "USER_BOOK_FINISHED_AT")
    private LocalDate finishedAt;

    @Column(name = "USER_BOOK_CURRENT_PAGE")
    private Integer currentPage;

    @Column(name = "USER_BOOK_RATING")
    private Integer rating;

    @Column(name = "USER_BOOK_NOTES", length = 2000)
    private String notes;

    @Builder.Default
    @Column(name = "USER_BOOK_FAVORITE", nullable = false)
    private boolean favorite = false;

    @PrePersist
    void onCreate() {
        if (addedAt == null) {
            addedAt = Instant.now();
        }
        if (statusReading == null) {
            statusReading = StatusReading.WANT_READ;
        }
    }
}
