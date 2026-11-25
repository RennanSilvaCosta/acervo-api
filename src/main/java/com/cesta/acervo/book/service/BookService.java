package com.cesta.acervo.book.service;

import com.cesta.acervo.book.dto.BookResponseDto;
import com.cesta.acervo.book.entity.Author;
import com.cesta.acervo.book.entity.Publisher;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public BookResponseDto createByIsbn(String isbn) {
        // Mantem o "X", digito verificador valido do ISBN-10
        var isbnNormalize = isbn.replaceAll("[^0-9Xx]", "").toUpperCase();
        var dbBook = this.bookRepository.findByIsbnCode(
                isbnNormalize,
                List.of(IdentifyTypeEnum.ISBN, IdentifyTypeEnum.ISBN_13)
        );

        if (dbBook.isPresent()) {
            return bookMapper.toResponse(dbBook.get());
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

        book.setAuthors(resolveAuthors(book.getAuthors()));
        book.setPublishers(resolvePublishers(book.getPublishers()));
        var entity = this.bookRepository.saveAndFlush(book);
        this.bookIdentificationRepository.saveAllAndFlush(entity.getBookIdentifications());

        return bookMapper.toResponse(entity);
    }

    private Set<Author> resolveAuthors(Set<Author> authors) {
        if (authors == null || authors.isEmpty()) {
            return authors;
        }

        Set<Author> resolved = new HashSet<>();
        for (Author author : authors) {
            Author persisted = this.authorRepository.findByNameIgnoreCase(author.getName())
                    .orElseGet(() -> this.authorRepository.save(author));
            if (persisted.getProfileUrl() == null && author.getProfileUrl() != null) {
                persisted.setProfileUrl(author.getProfileUrl());
            }
            resolved.add(persisted);
        }
        return resolved;
    }

    private Set<Publisher> resolvePublishers(Set<Publisher> publishers) {
        if (publishers == null || publishers.isEmpty()) {
            return publishers;
        }

        Set<Publisher> resolved = new HashSet<>();
        for (Publisher publisher : publishers) {
            Publisher persisted = this.publisherRepository.findByNameIgnoreCase(publisher.getName())
                    .orElseGet(() -> this.publisherRepository.save(publisher));
            resolved.add(persisted);
        }
        return resolved;
    }
}
