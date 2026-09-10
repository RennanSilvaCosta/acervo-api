package com.cesta.acervo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Configuration
public class GoogleBooksClientConfig {

    @Bean(name = "googleBooks")
    public WebClient googleBooksWebClient() {
        return WebClient.builder()
                .baseUrl("https://www.googleapis.com/books/v1")
                .build();
    }

    @Bean(name = "openLibrary")
    public WebClient openLibraryWebClient() {
        return WebClient.builder()
                .baseUrl("https://openlibrary.org/api")
                .build();
    }
}
