package com.cesta.acervo.userbook.dto;

import com.cesta.acervo.userbook.enums.StatusReading;
import jakarta.validation.constraints.NotNull;

public record AddUserBookRequest(
        @NotNull StatusReading statusReading
) {
}
