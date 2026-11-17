package com.cesta.acervo.book.dto;

import java.util.List;

public record BookResponseDto(
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
