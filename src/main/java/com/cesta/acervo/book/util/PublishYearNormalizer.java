package com.cesta.acervo.book.util;

import java.time.Year;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reduz a data de publicacao vinda das fontes externas ao ano com quatro digitos.
 * Exemplos: "2017" -> "2017", "May 12, 2019" -> "2019", "2019-05-12" -> "2019", "s.d." -> null.
 */
public final class PublishYearNormalizer {

    private static final int MIN_YEAR = 1450;
    private static final Pattern FOUR_DIGITS = Pattern.compile("(?<!\\d)(\\d{4})(?!\\d)");

    private PublishYearNormalizer() {
    }

    public static String normalize(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) {
            return null;
        }

        int maxYear = Year.now().getValue() + 1;
        Matcher matcher = FOUR_DIGITS.matcher(rawDate);
        while (matcher.find()) {
            int year = Integer.parseInt(matcher.group(1));
            if (year >= MIN_YEAR && year <= maxYear) {
                return String.valueOf(year);
            }
        }
        return null;
    }
}
