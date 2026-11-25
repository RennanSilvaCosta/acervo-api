package com.cesta.acervo.userbook.mapper;

import com.cesta.acervo.book.entity.Author;
import com.cesta.acervo.book.entity.Book;
import com.cesta.acervo.book.entity.Cover;
import com.cesta.acervo.book.entity.Publisher;
import com.cesta.acervo.book.entity.BookIdentification;
import com.cesta.acervo.book.enums.IdentifyTypeEnum;
import com.cesta.acervo.book.util.PublishYearNormalizer;
import com.cesta.acervo.userbook.dto.UserBookDetailResponse;
import com.cesta.acervo.userbook.dto.UserBookResponse;
import com.cesta.acervo.userbook.entity.UserBook;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class UserBookMapper {

    private static final Pattern ISBN_10 = Pattern.compile("\\d{9}[\\dX]");
    private static final Pattern ISBN_13 = Pattern.compile("\\d{13}");

    public UserBookResponse toResponse(UserBook userBook) {
        Book book = userBook.getBook();
        return new UserBookResponse(
                userBook.getId(),
                userBook.getUser().getId(),
                userBook.getStatusReading(),
                userBook.getAddedAt(),
                userBook.getStartedAt(),
                userBook.getFinishedAt(),
                userBook.getCurrentPage(),
                userBook.getRating(),
                userBook.getNotes(),
                userBook.isFavorite(),
                new UserBookResponse.BookSummary(
                        book.getId(),
                        book.getTitle(),
                        book.getDescription(),
                        PublishYearNormalizer.normalize(book.getPublishYear()),
                        book.getPageCount(),
                        authorNames(book),
                        publisherNames(book),
                        coverUrl(book.getCover())
                )
        );
    }

    public UserBookDetailResponse toDetailResponse(UserBook userBook) {
        Book book = userBook.getBook();
        return new UserBookDetailResponse(
                new UserBookDetailResponse.BookDetail(
                        book.getId(),
                        book.getTitle(),
                        book.getDescription(),
                        authorNames(book),
                        publisherNames(book),
                        PublishYearNormalizer.normalize(book.getPublishYear()),
                        book.getPageCount(),
                        book.getLanguage(),
                        identifier(book, IdentifyTypeEnum.ISBN, ISBN_10),
                        identifier(book, IdentifyTypeEnum.ISBN_13, ISBN_13),
                        coverUrl(book.getCover())
                ),
                new UserBookDetailResponse.Reading(
                        userBook.getStatusReading(),
                        userBook.isFavorite(),
                        userBook.getAddedAt(),
                        userBook.getStartedAt(),
                        userBook.getFinishedAt(),
                        userBook.getCurrentPage(),
                        userBook.getRating(),
                        userBook.getNotes()
                )
        );
    }

    /**
     * Primeiro identificador do tipo pedido que tem formato valido de ISBN.
     * O formato e conferido porque livros gravados antes da correcao podem ter codigos internos do Google como ISBN_13.
     */
    private String identifier(Book book, IdentifyTypeEnum type, Pattern format) {
        if (book.getBookIdentifications() == null) {
            return null;
        }
        return book.getBookIdentifications().stream()
                .filter(id -> id.getIdentifyType() == type)
                .map(BookIdentification::getIdentify)
                .filter(Objects::nonNull)
                .filter(value -> format.matcher(value).matches())
                .sorted()
                .findFirst()
                .orElse(null);
    }

    private List<String> authorNames(Book book) {
        if (book.getAuthors() == null) {
            return List.of();
        }
        return book.getAuthors().stream().map(Author::getName).toList();
    }

    private List<String> publisherNames(Book book) {
        if (book.getPublishers() == null) {
            return List.of();
        }
        return book.getPublishers().stream().map(Publisher::getName).toList();
    }

    private String coverUrl(Cover cover) {
        if (cover == null) {
            return null;
        }
        if (cover.getUrlMedium() != null) {
            return cover.getUrlMedium();
        }
        if (cover.getUrlLarge() != null) {
            return cover.getUrlLarge();
        }
        return cover.getUrlSmall();
    }
}
