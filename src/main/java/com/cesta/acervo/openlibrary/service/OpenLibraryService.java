package com.cesta.acervo.openlibrary.service;

import com.cesta.acervo.openlibrary.dto.OpenLibraryBookResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class OpenLibraryService {

    private final WebClient webClient;

    public OpenLibraryService(@Qualifier("openLibrary") WebClient openlibraryWebClient) {
        this.webClient = openlibraryWebClient;
    }

    public OpenLibraryBookResponse searchByIsbn(String isbn) {
        Map<String, OpenLibraryBookResponse> response =
                webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/books")
                                .queryParam("bibkeys", "ISBN:" + isbn)
                                .queryParam("format", "json")
                                .queryParam("jscmd", "data")
                                .build())
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<
                                Map<String, OpenLibraryBookResponse>>() {})
                        .block();

        if (response == null || response.isEmpty()) {
            return null;
        }

        return response.values().iterator().next();
    }
}
