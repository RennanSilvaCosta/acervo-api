package com.cesta.acervo.userbook.dto;

import com.cesta.acervo.userbook.enums.StatusReading;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record UserBookDetailResponse(
        BookDetail book,
        Reading reading
) {

    public record BookDetail(
            Long id,
            String title,
            String synopsis,
            List<String> authors,
            List<String> publishers,
            String publishYear,
            Integer pageCount,
            String language,
            String isbn10,
            String isbn13,
            String coverUrl
    ) {
    }

    public record Reading(
            StatusReading status,
            boolean favorite,
            Instant addedAt,
            LocalDate startedAt,
            LocalDate finishedAt,
            Integer currentPage,
            Integer rating,
            String notes
    ) {
    }
}
