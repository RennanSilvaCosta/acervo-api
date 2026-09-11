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
@Entity(name = "TB_PUBLISHER")
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "PUBLISHER_ID")
    private Long id;

    @Column(name = "PUBLISHER_NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "PUBLISHER_DESCRIPTION", length = 4000)
    private String description;

    @Column(name = "PUBLISHER_COUNTRY")
    private String country;

    @Column(name = "PUBLISHER_CITY")
    private String city;

    @Column(name = "PUBLISHER_WEBSITE_URL")
    private String websiteUrl;

    @Column(name = "PUBLISHER_LOGO_URL")
    private String logoUrl;

    @Column(name = "PUBLISHER_FOUNDED_YEAR")
    private Integer foundedYear;

    @Builder.Default
    @ManyToMany(mappedBy = "publishers", fetch = FetchType.LAZY)
    private Set<Book> books = new HashSet<>();
}
