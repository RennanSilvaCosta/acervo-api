package com.cesta.acervo.googlebook.service;

import com.cesta.acervo.googlebook.dto.GoogleBooksResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class GoogleBooksService {

    private final WebClient webClient;

    public GoogleBooksService(WebClient googleBooksWebClient) {
        this.webClient = googleBooksWebClient;
    }

    public GoogleBooksResponse searchByIsbn(String isbn) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/volumes")
                            .queryParam("q", "isbn:" + isbn)
                            .build()
                    )
                    .retrieve()
                    .bodyToMono(GoogleBooksResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            throw new RuntimeException(
                    "Erro ao consumir Google Books API: " + ex.getResponseBodyAsString(),
                    ex
            );
        }
    }
}
