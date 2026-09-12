package com.cesta.acervo.book.entity;

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
@Entity(name = "TB_AUTHOR")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "AUTHOR_ID")
    private Long id;

    @Column(name = "AUTHOR_NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "AUTHOR_BIOGRAPHY", length = 4000)
    private String biography;

    @Column(name = "AUTHOR_BIRTH_DATE")
    private LocalDate birthDate;

    @Column(name = "AUTHOR_DEATH_DATE")
    private LocalDate deathDate;

    @Column(name = "AUTHOR_NATIONALITY")
    private String nationality;

    @Column(name = "AUTHOR_PHOTO_URL")
    private String photoUrl;

    @Column(name = "AUTHOR_PROFILE_URL")
    private String profileUrl;

    @Builder.Default
    @ManyToMany(mappedBy = "authors", fetch = FetchType.LAZY)
    private Set<Book> books = new HashSet<>();
}
