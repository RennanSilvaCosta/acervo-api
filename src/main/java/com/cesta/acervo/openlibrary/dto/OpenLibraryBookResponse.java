package com.cesta.acervo.openlibrary.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
@Setter
public class OpenLibraryBookResponse {

    private String title;
    private String publish_date;
    private Cover cover;
    private Integer number_of_pages;
    private List<Author> authors;
    private Identifie identifiers;

    @Getter
    @Setter
    public static class Cover {
        private String small;
        private String medium;
        private String large;
    }

    @Getter
    @Setter
    public static class Author {
        private String url;
        private String name;
    }

    @Getter
    @Setter
    public static class Identifie {
        private List<String> openlibrary;
    }
}