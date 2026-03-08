package com.cesta.acervo.googlebook.service;

import com.cesta.acervo.googlebook.dto.GoogleBookResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GoogleBooksService {

    private final WebClient webClient;

    public GoogleBooksService(@Qualifier("googleBooks") WebClient googleBooksWebClient) {
        this.webClient = googleBooksWebClient;
    }

    public GoogleBookResponse searchByIsbn(String isbn) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/volumes")
                        .queryParam("q", "isbn:" + isbn)
                        .build())
                .retrieve()
                .bodyToMono(GoogleBookResponse.class)
                .block();
    }
}