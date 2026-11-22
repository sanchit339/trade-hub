package com.sanchit.settlement.validation;

import com.sanchit.settlement.model.TradeMessage;
import org.springframework.stereotype.Component;
import jakarta.validation.Validator;
import java.util.List;

/**
 * - Takes TradeMessage - Does Just schema validation
 * */
@Component
public class TradeValidator {
    private final Validator validator;

    public TradeValidator(Validator validator){
        this.validator = validator;
    }

    public TradeMessage validate(TradeMessage trade){
        if(trade == null){
            throw new TradeValidationException(List.of("request body is Empty"));
        }

        var violation = validator.validate(trade);
        // Collect all the non-validated part
        if(!violation.isEmpty()){
            throw new TradeValidationException(violation.stream()
                    .map(v -> v.getPropertyPath() + " : " + v.getMessage())
                    .sorted()
                    .toList());
        }

        return trade;
    }
}
