package com.cesta.acervo.book.service;

import com.cesta.acervo.book.enums.IdentifyTypeEnum;
import com.cesta.acervo.book.mapper.BookMapper;
import com.cesta.acervo.book.repository.AuthorRepository;
import com.cesta.acervo.book.repository.BookIdentificationRepository;
import com.cesta.acervo.book.repository.BookRepository;
import com.cesta.acervo.book.repository.PublisherRepository;
import com.cesta.acervo.googlebook.dto.GoogleBookResponse;
import com.cesta.acervo.googlebook.service.GoogleBooksService;
import com.cesta.acervo.openlibrary.dto.OpenLibraryBookResponse;
import com.cesta.acervo.openlibrary.service.OpenLibraryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class BookService {

    private final GoogleBooksService googleBooksService;
    private final OpenLibraryService openLibraryService;
    private final BookMapper bookMapper;
    private final Executor executor;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublisherRepository publisherRepository;
    private final BookIdentificationRepository bookIdentificationRepository;

    public BookService(GoogleBooksService googleBooksService,
                       OpenLibraryService openLibraryService,
                       BookMapper bookMapper,
                       @Qualifier("taskExecutor") Executor executor,
                       BookRepository bookRepository,
                       AuthorRepository authorRepository,
                       PublisherRepository publisherRepository,
                       BookIdentificationRepository bookIdentificationRepository) {
        this.googleBooksService = googleBooksService;
        this.openLibraryService = openLibraryService;
        this.bookMapper = bookMapper;
        this.executor = executor;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.publisherRepository = publisherRepository;
        this.bookIdentificationRepository = bookIdentificationRepository;
    }

    @Transactional
    public Long createByIsbn(String isbn) {
        var isbnNormalize = isbn.replaceAll("[^0-9]", "");
        var dbBook = this.bookRepository.findByIsbnCode(
                isbnNormalize,
                List.of(IdentifyTypeEnum.ISBN, IdentifyTypeEnum.ISBN_13)
        );

        if (dbBook.isPresent()) {
            return dbBook.get().getId();
        }

        CompletableFuture<GoogleBookResponse> googleFuture = CompletableFuture
                .supplyAsync(() -> googleBooksService.searchByIsbn(isbnNormalize), executor)
                .exceptionally(ex -> null);

        CompletableFuture<OpenLibraryBookResponse> openFuture = CompletableFuture
                .supplyAsync(() -> openLibraryService.searchByIsbn(isbnNormalize), executor)
                .exceptionally(ex -> null);

        var book = bookMapper.toEntity(googleFuture.join(), openFuture.join(), isbnNormalize);
        if (book.getTitle() == null) {
            throw new IllegalStateException("Não foi possível obter dados do livro para o ISBN " + isbnNormalize);
        }

        var entity = this.bookRepository.saveAndFlush(book);
        this.bookIdentificationRepository.saveAllAndFlush(entity.getBookIdentifications());
        this.authorRepository.saveAll(entity.getAuthors());
        this.publisherRepository.saveAll(entity.getPublishers());

        return entity.getId();
    }
}
