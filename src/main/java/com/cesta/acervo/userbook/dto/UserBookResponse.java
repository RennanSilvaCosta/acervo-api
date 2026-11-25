package com.cesta.acervo.userbook.dto;

import com.cesta.acervo.userbook.enums.StatusReading;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record UserBookResponse(
        Long id,
        Long userId,
        StatusReading statusReading,
        Instant addedAt,
        LocalDate startedAt,
        LocalDate finishedAt,
        Integer currentPage,
        Integer rating,
        String notes,
        boolean favorite,
        BookSummary book
) {

    public record BookSummary(
            Long id,
            String title,
            String description,
            String publishYear,
            Integer pageCount,
            List<String> authors,
            List<String> publishers,
            String coverUrl
    ) {
    }
}
