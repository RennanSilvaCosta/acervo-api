package com.cesta.acervo.book.mapper;

import com.cesta.acervo.book.dto.BookResponseDto;
import com.cesta.acervo.book.entity.Author;
import com.cesta.acervo.book.entity.Book;
import com.cesta.acervo.book.entity.BookIdentification;
import com.cesta.acervo.book.entity.Cover;
import com.cesta.acervo.book.entity.Publisher;
import com.cesta.acervo.book.enums.IdentifyTypeEnum;
import com.cesta.acervo.book.util.PublishYearNormalizer;
import com.cesta.acervo.googlebook.dto.GoogleBookResponse;
import com.cesta.acervo.googlebook.dto.GoogleBookResponse.VolumeInfo;
import com.cesta.acervo.googlebook.dto.GoogleBookResponse.VolumeItem;
import com.cesta.acervo.openlibrary.dto.OpenLibraryBookResponse;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BookMapper {

    public Book toEntity(GoogleBookResponse google, OpenLibraryBookResponse open, String isbn) {
        VolumeItem googleItem = googleItem(google);
        VolumeInfo googleBook = googleItem != null && googleItem.getVolumeInfo() != null
                ? googleItem.getVolumeInfo()
                : new VolumeInfo();
        OpenLibraryBookResponse openBook = open != null ? open : new OpenLibraryBookResponse();

        Book book = new Book();
        book.setTitle(prefer(googleBook.getTitle(), openBook.getTitle()));
        book.setDescription(prefer(googleBook.getDescription(), null));
        book.setPageCount(prefer(googleBook.getPageCount(), openBook.getNumber_of_pages()));
        book.setPublishYear(prefer(
                PublishYearNormalizer.normalize(googleBook.getPublishedDate()),
                PublishYearNormalizer.normalize(openBook.getPublish_date())
        ));
        book.setLanguage(normalizeLanguage(googleBook.getLanguage()));
        book.setCover(toCover(openBook.getCover()));

        Map<String, String> openLibraryAuthorUrls = openBook.getAuthors() == null
                ? Map.of()
                : openBook.getAuthors().stream()
                .filter(author -> author != null && isPresent(author.getName()))
                .collect(Collectors.toMap(
                        author -> author.getName().trim().toLowerCase(),
                        author -> author.getUrl() == null ? "" : author.getUrl(),
                        (existing, ignored) -> existing
                ));

        List<String> authorNames = prefer(
                googleBook.getAuthors(),
                openBook.getAuthors() == null ? null : openBook.getAuthors().stream()
                        .filter(Objects::nonNull)
                        .map(OpenLibraryBookResponse.Author::getName)
                        .toList()
        );
        if (authorNames != null) {
            authorNames.stream()
                    .filter(this::isPresent)
                    .forEach(name -> {
                        Author author = new Author();
                        author.setName(name.trim());
                        String profileUrl = openLibraryAuthorUrls.get(name.trim().toLowerCase());
                        if (isPresent(profileUrl)) {
                            author.setProfileUrl(profileUrl);
                        }
                        book.getAuthors().add(author);
                    });
        }

        List<String> publisherNames = prefer(
                googleBook.getPublisher() == null ? null : List.of(googleBook.getPublisher()),
                openBook.getPublishers() == null ? null : openBook.getPublishers().stream()
                        .filter(Objects::nonNull)
                        .map(OpenLibraryBookResponse.Publisher::getName)
                        .toList()
        );
        if (publisherNames != null) {
            publisherNames.stream()
                    .filter(this::isPresent)
                    .forEach(name -> {
                        Publisher publisher = new Publisher();
                        publisher.setName(name.trim());
                        book.getPublishers().add(publisher);
                    });
        }

        addIdentification(book, isbn.length() >= 13 ? IdentifyTypeEnum.ISBN_13 : IdentifyTypeEnum.ISBN, isbn);

        if (googleBook.getIndustryIdentifiers() != null) {
            googleBook.getIndustryIdentifiers().forEach(item -> {
                if (item == null) {
                    return;
                }
                // O Google tambem devolve identificadores do tipo "OTHER" (codigos internos), que nao sao ISBN
                IdentifyTypeEnum type = switch (item.getType() == null ? "" : item.getType()) {
                    case "ISBN_10" -> IdentifyTypeEnum.ISBN;
                    case "ISBN_13" -> IdentifyTypeEnum.ISBN_13;
                    default -> null;
                };
                addIdentification(book, type, item.getIdentifier());
            });
        }

        if (googleItem != null) {
            addIdentification(book, IdentifyTypeEnum.GOOGLE_BOOK, googleItem.getId());
        }

        if (openBook.getIdentifiers() != null
                && openBook.getIdentifiers().getOpenlibrary() != null
                && !openBook.getIdentifiers().getOpenlibrary().isEmpty()) {
            addIdentification(book, IdentifyTypeEnum.OPEN_LIBRARY, openBook.getIdentifiers().getOpenlibrary().getFirst());
        }

        return book;
    }

    public BookResponseDto toResponse(Book book) {
        return new BookResponseDto(
                book.getId(),
                book.getTitle(),
                book.getDescription(),
                PublishYearNormalizer.normalize(book.getPublishYear()),
                book.getPageCount(),
                names(book.getAuthors(), Author::getName),
                names(book.getPublishers(), Publisher::getName),
                coverUrl(book.getCover())
        );
    }

    private Cover toCover(OpenLibraryBookResponse.Cover openCover) {
        if (openCover == null) {
            return null;
        }

        Cover cover = new Cover();
        cover.setUrlSmall(prefer(openCover.getSmall(), null));
        cover.setUrlMedium(prefer(openCover.getMedium(), null));
        cover.setUrlLarge(prefer(openCover.getLarge(), null));

        if (cover.getUrlSmall() == null && cover.getUrlMedium() == null && cover.getUrlLarge() == null) {
            return null;
        }

        return cover;
    }

    private String normalizeLanguage(String language) {
        return isPresent(language) ? language.trim().toLowerCase() : null;
    }

    private VolumeItem googleItem(GoogleBookResponse google) {
        if (google == null || google.getItems() == null || google.getItems().isEmpty()) {
            return null;
        }
        return google.getItems().getFirst();
    }

    private void addIdentification(Book book, IdentifyTypeEnum type, String value) {
        if (type == null || !isPresent(value)) {
            return;
        }

        boolean alreadyPresent = book.getBookIdentifications().stream()
                .anyMatch(existing -> existing.getIdentifyType() == type && value.equals(existing.getIdentify()));
        if (alreadyPresent) {
            return;
        }

        BookIdentification identification = new BookIdentification();
        identification.setIdentifyType(type);
        identification.setIdentify(value);
        identification.setBook(book);
        book.getBookIdentifications().add(identification);
    }

    private <T> T prefer(T googleValue, T openLibraryValue) {
        return isPresent(googleValue) ? googleValue : (isPresent(openLibraryValue) ? openLibraryValue : null);
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

    private <T> List<String> names(Set<T> values, Function<T, String> name) {
        if (values == null) {
            return List.of();
        }
        return values.stream().map(name).toList();
    }

    private boolean isPresent(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof String text) {
            return !text.isBlank();
        }
        if (value instanceof Collection<?> collection) {
            return !collection.isEmpty();
        }
        return true;
    }
}
