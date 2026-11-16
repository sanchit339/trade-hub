package com.sanchit.settlement.model;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TradeMessage (
        @NotBlank @Pattern(regexp = "[A-Za-z0-9_-]{1,64}",message = "must be 1-64 chars of letters, digit, _ or -")
        String tradeId,
        @NotNull TradeType tradeType,
        @NotBlank @Size(max = 32) String instrument,
        @NotNull Side side,
        @NotNull @Positive BigDecimal quantity,
        @NotNull @Positive BigDecimal price,
        @NotBlank @Pattern(regexp = "[A-Z]{3}", message = "Must be a 3 - letter uppercase code") String currency,
        @NotNull @PastOrPresent LocalDate tradeDate,
        String source
        ) {
    public TradeMessage withSource(String newSource){
        return new TradeMessage(tradeId, tradeType, instrument, side, quantity, price, currency, tradeDate, newSource);
    }
}
