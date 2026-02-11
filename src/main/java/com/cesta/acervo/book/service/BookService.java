package com.cesta.acervo.book.service;

import com.cesta.acervo.book.dto.BookResponseDto;
import com.cesta.acervo.googlebook.dto.GoogleBooksResponse;
import com.cesta.acervo.googlebook.service.GoogleBooksService;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final GoogleBooksService googleBooksService;

    public BookService(GoogleBooksService googleBooksService) {
        this.googleBooksService = googleBooksService;
    }

    public BookResponseDto createByIsbn(String isbn) {
        GoogleBooksResponse googleBook = this.googleBooksService.searchByIsbn(isbn);
        return null;
    }

}
