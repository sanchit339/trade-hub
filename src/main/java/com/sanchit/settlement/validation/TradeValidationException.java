package com.sanchit.settlement.validation;

import java.util.List;


// Central Validation Exception handler
public class TradeValidationException extends RuntimeException{
    private final List<String> errors;

    public TradeValidationException(List<String> errors){
        super("Trade validation field:" + errors); // super Exception and Message
        this.errors = errors;
    }

    public List<String> getErrors(){ return errors; }
}
