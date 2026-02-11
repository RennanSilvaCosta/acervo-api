package com.cesta.acervo.book.controller;

import com.cesta.acervo.book.dto.BookResponseDto;
import com.cesta.acervo.book.service.BookService;
import org.hibernate.validator.constraints.ISBN;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
@Validated
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/isbn/{isbn}")
    public BookResponseDto createByIsbn(@PathVariable @ISBN String isbn) {
        return bookService.createByIsbn(isbn);
    }

}
