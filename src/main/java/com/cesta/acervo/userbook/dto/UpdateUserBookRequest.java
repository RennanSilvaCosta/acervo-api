package com.cesta.acervo.userbook.dto;

import com.cesta.acervo.book.enums.StatusReading;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

public record UpdateUserBookRequest(
        StatusReading statusReading,
        LocalDate startedAt,
        LocalDate finishedAt,
        @Min(0) Integer currentPage,
        @Min(1) @Max(5) Integer rating,
        String notes,
        Boolean favorite
) {
}
