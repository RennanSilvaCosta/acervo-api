package com.cesta.acervo.userbook.mapper;

import com.cesta.acervo.book.entity.Author;
import com.cesta.acervo.book.entity.Book;
import com.cesta.acervo.book.entity.Cover;
import com.cesta.acervo.book.entity.Publisher;
import com.cesta.acervo.userbook.dto.UserBookResponse;
import com.cesta.acervo.userbook.entity.UserBook;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserBookMapper {

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
                        book.getPublishYear(),
                        book.getPageCount(),
                        authorNames(book),
                        publisherNames(book),
                        coverUrl(book.getCover())
                )
        );
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
