package com.cesta.acervo.googlebook.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
@Setter
public class GoogleBooksResponse {
    private List<VolumeItem> items;

    @Getter
    @Setter
    public static class VolumeItem {
        private VolumeInfo volumeInfo;
        private String id;
    }

    @Getter
    @Setter
    public static class VolumeInfo {

        private String title;
        private List<String> authors;
        private String description;
        private Integer pageCount;
        private List<Identifier> industryIdentifiers;
    }

    @Getter
    @Setter
    public static class Identifier {
        private String type;
        private String identifier;
    }
}
