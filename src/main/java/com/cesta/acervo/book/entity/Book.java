package com.cesta.acervo.book.entity;

import com.cesta.acervo.book.enums.IdentifyTypeEnum;
import com.cesta.acervo.book.enums.StatusReading;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "TB_BOOK")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "BOOK_ID")
    private Long id;

    @Column(name = "BOOK_TITLE", nullable = false)
    private String title;

    @Singular
    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private Set<BookIdentification> bookIdentifications = new HashSet<>();

    @Singular
    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private Set<Author> authors = new HashSet<>();

    @Singular
    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private Set<Publisher> publishers = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "BOOK_COVER_ID")
    private Cover cover;

    @Column(name = "BOOK_PUBLISH_DATE", nullable = false)
    private LocalDate publishDate;

    @Column(name = "BOOK_STATUS_READING")
    @Enumerated(EnumType.STRING)
    private StatusReading statusReading;

    @Column(name = "BOOK_DESCRIPTION")
    private String description;

    @Column(name = "BOOK_CREATE_DATE")
    private LocalDate createDate;

}
