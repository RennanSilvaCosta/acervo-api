package com.cesta.acervo.book.service;

import com.cesta.acervo.book.entity.Author;
import com.cesta.acervo.book.entity.Book;
import com.cesta.acervo.book.entity.BookIdentification;
import com.cesta.acervo.book.entity.Cover;
import com.cesta.acervo.book.enums.IdentifyTypeEnum;
import com.cesta.acervo.book.repository.AuthorRepository;
import com.cesta.acervo.book.repository.BookIdentificationRepository;
import com.cesta.acervo.book.repository.BookRepository;
import com.cesta.acervo.googlebook.dto.GoogleBookResponse;
import com.cesta.acervo.googlebook.service.GoogleBooksService;
import com.cesta.acervo.openlibrary.dto.OpenLibraryBookResponse;
import com.cesta.acervo.openlibrary.service.OpenLibraryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class BookService {

    private final GoogleBooksService googleBooksService;
    private final OpenLibraryService openLibraryService;
    private final Executor executor;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookIdentificationRepository bookIdentificationRepository;

    public BookService(GoogleBooksService googleBooksService, OpenLibraryService openLibraryService, @Qualifier("taskExecutor") Executor executor, BookRepository bookRepository, AuthorRepository authorRepository, BookIdentificationRepository bookIdentificationRepository) {
        this.googleBooksService = googleBooksService;
        this.openLibraryService = openLibraryService;
        this.executor = executor;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookIdentificationRepository = bookIdentificationRepository;
    }

    @Transactional
    public Long createByIsbn(String isbn) {
        CompletableFuture<GoogleBookResponse> googleFuture =
                CompletableFuture.supplyAsync(
                        () -> googleBooksService.searchByIsbn(isbn),
                        executor
                );

        CompletableFuture<OpenLibraryBookResponse> openFuture =
                CompletableFuture.supplyAsync(
                        () -> openLibraryService.searchByIsbn(isbn),
                        executor
                );
        CompletableFuture.allOf(googleFuture, openFuture).join();

        GoogleBookResponse google = googleFuture.join();
        OpenLibraryBookResponse open = openFuture.join();

        var book = mergeBookData(google, open);
        var identifies = mergeIdentifies(google, open);
        var author = mergeAuthor(open);

        var entity = this.bookRepository.saveAndFlush(book);

        identifies.forEach((identifie) -> identifie.setBook(entity));
        author.setBook(entity);

        this.bookIdentificationRepository.saveAllAndFlush(identifies);
        this.authorRepository.save(author);

        return entity.getId();
    }

    private List<BookIdentification> mergeIdentifies(GoogleBookResponse google,
                                                     OpenLibraryBookResponse open) {
        List<BookIdentification> listIdentification = new ArrayList<>();
        var googleBook = google.getItems().getFirst().getVolumeInfo();
        var googleIdentifies = googleBook.getIndustryIdentifiers();
        var openLibraryIdentifie = open.getIdentifiers().getOpenlibrary().getFirst();

        googleIdentifies.forEach(item -> {
            BookIdentification identification = new BookIdentification();
            identification.setIdentify(item.getIdentifier());
            identification.setIdentifyType(item.getType().equals("ISBN_10") ? IdentifyTypeEnum.IBSN : IdentifyTypeEnum.IBSN_13);
            listIdentification.add(identification);
        });

        BookIdentification googleId = new BookIdentification();
        googleId.setIdentifyType(IdentifyTypeEnum.GOOGLE_BOOK);
        googleId.setIdentify(google.getItems().getFirst().getId());
        listIdentification.add(googleId);

        BookIdentification openId = new BookIdentification();
        openId.setIdentifyType(IdentifyTypeEnum.OPEN_LIBRARY);
        openId.setIdentify(openLibraryIdentifie);
        listIdentification.add(openId);

        return listIdentification;
    }

    private Author mergeAuthor(OpenLibraryBookResponse open) {
        Author author = new Author();
        var openAuthor = open.getAuthors().getFirst();
        author.setName(openAuthor.getName());

        return author;
    }

    private Book mergeBookData(GoogleBookResponse google,
                               OpenLibraryBookResponse open) {
        Book book = new Book();
        Cover cover = new Cover();

        var googleBook = google.getItems().getFirst().getVolumeInfo();

        cover.setUrlSmall(open.getCover().getSmall());
        cover.setUrlMedium(open.getCover().getMedium());
        cover.setUrlLarge(open.getCover().getLarge());

        book.setTitle(googleBook.getTitle());
        book.setDescription(googleBook.getDescription());
        book.setPageCount(googleBook.getPageCount());
        book.setPublishYear(open.getPublish_date());
        book.setCover(cover);

        return book;
    }

}
