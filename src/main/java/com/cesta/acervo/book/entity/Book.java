package com.cesta.acervo.book.entity;

import jakarta.persistence.*;
import lombok.*;

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
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "TB_BOOK_AUTHOR",
            joinColumns = @JoinColumn(name = "BOOK_ID"),
            inverseJoinColumns = @JoinColumn(name = "AUTHOR_ID")
    )
    private Set<Author> authors = new HashSet<>();

    @Singular
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "TB_BOOK_PUBLISHER",
            joinColumns = @JoinColumn(name = "BOOK_ID"),
            inverseJoinColumns = @JoinColumn(name = "PUBLISHER_ID")
    )
    private Set<Publisher> publishers = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "BOOK_COVER_ID")
    private Cover cover;

    @Column(name = "BOOK_PUBLISH_YEAR")
    private String publishYear;

    @Column(name = "BOOK_DESCRIPTION",length = 4000)
    private String description;

    @Column(name = "BOOK_PAGE_COUNT")
    private Integer pageCount;

    /** Codigo ISO 639-1 do idioma (ex.: "pt", "en"), quando a fonte informa. */
    @Column(name = "BOOK_LANGUAGE", length = 10)
    private String language;

}
