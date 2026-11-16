package com.sanchit.settlement.routes;

import tools.jackson.core.JacksonException;
import com.sanchit.settlement.model.TradeMessage;
import com.sanchit.settlement.validation.TradeValidationException;
import com.sanchit.settlement.validation.TradeValidator;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class RestIngestRoute extends RouteBuilder {

    private final TradeValidator tradeValidator;

    public RestIngestRoute(TradeValidator tradeValidator){
        this.tradeValidator = tradeValidator;
    }


    @Override
    public void configure() throws Exception {

        onException(TradeValidationException.class) // TradeValidation
                .handled(true)
                .process(e -> {
                    var ex = e.getProperty(Exchange.EXCEPTION_CAUGHT, TradeValidationException.class);
                    e.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
                    e.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
                    e.getMessage().setBody(Map.of("error", "validation-failed" , "details" , ex.getErrors()));
                })
                .marshal("tradeJson");

        onException(JacksonException.class)
                .handled(true)
                .process(e -> {
                    e.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, 400);
                    e.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
                    e.getMessage().setBody(Map.of("Error" , "malformed_json")); // some issue in the JSON
                })
                .marshal("tradeJson");

        from("platform-http:/api/trades?httpMethodRestrict=POST&consumes=application/json")
                .routeId("rest-ingest")
                .unmarshal("tradeJson") // json to JavaObject
                .bean(tradeValidator, "validate") // passing through processor
                .process(e -> e.getMessage().setBody(e.getMessage().getBody(TradeMessage.class).withSource("REST")))
                .to("direct:publish-trade")
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(202))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody(simple("{\"status\":\"ACCEPTED\",\"tradeId\":\"${exchangeProperty.tradeId}\"}"));
    }
}
