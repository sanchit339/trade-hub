package com.sanchit.settlement.model;


import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.math.BigDecimal;
import java.time.LocalDate;

@CsvRecord(separator = ",")
public class TradeCsvRecord {
    @DataField(pos = 1, trim = true) private String tradeId;
    @DataField(pos = 2, trim = true) private String tradeType;
    @DataField(pos = 3, trim = true) private String instrument;
    @DataField(pos = 4, trim = true) private String side;
    @DataField(pos = 5, trim = true) private String quantity;
    @DataField(pos = 6, trim = true) private String price;
    @DataField(pos = 7, trim = true) private String currency;
    @DataField(pos = 8, trim = true) private String tradeDate;


    public TradeMessage toMessage(){
        return new TradeMessage(
                tradeId,
                TradeType.valueOf(req(tradeType, "tradeType").toUpperCase()),
                instrument,
                Side.valueOf(req(side, "side").toUpperCase()),
                new BigDecimal(req(quantity, "quantity")),
                new BigDecimal(req(price, "price")),
                currency,
                LocalDate.parse(req(tradeDate, "tradeDate"))
                , null);
    }

    private static String req(String value, String field){
        if(value == null || value.isBlank()){
            throw new IllegalArgumentException(field + " is Missing");
        }
        return value.trim(); // removing trailing spaces
    }

}
