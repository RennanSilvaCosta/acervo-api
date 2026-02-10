package com.cesta.acervo.book.entity;

import com.cesta.acervo.book.enums.IdentifyTypeEnum;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "TB_BOOK_IDENTIFY")
public class BookIdentification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IdentifyTypeEnum identifyType;

    @Column(nullable = false)
    private String identify;

}
