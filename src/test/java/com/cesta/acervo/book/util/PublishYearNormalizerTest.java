package com.cesta.acervo.book.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PublishYearNormalizerTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "2017|2017",
            "May 12, 2019|2019",
            "2019-05-12|2019",
            "2019-05|2019",
            "c1998|1998",
            "[2005?]|2005",
            "12/03/1987|1987",
            "  2001  |2001"
    })
    void extractsYear(String raw, String expected) {
        assertEquals(expected, PublishYearNormalizer.normalize(raw));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "s.d.", "sem data", "123", "20190", "1200", "9999"})
    void returnsNullWhenThereIsNoPlausibleYear(String raw) {
        assertNull(PublishYearNormalizer.normalize(raw));
    }

    @Test
    void acceptsNextYear() {
        String nextYear = String.valueOf(Year.now().getValue() + 1);
        assertEquals(nextYear, PublishYearNormalizer.normalize("Pre-order " + nextYear));
    }

    @Test
    void skipsImplausibleNumberAndUsesNextYear() {
        assertEquals("2003", PublishYearNormalizer.normalize("Edition 1000, 2003"));
    }
}
