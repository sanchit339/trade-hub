package com.sanchit.settlement.routes;

import com.sanchit.settlement.model.TradeMessage;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.springframework.stereotype.Component;

// It receives a TradeMessage, turns it into JSON, and publishes it to the Kafka topic trades.inbound, keyed by tradeId.

@Component
public class PublishRoute extends RouteBuilder {


    @Override
    public void configure() throws Exception {
        from("direct:publish-trade")
                .routeId("publish-trader")
                .process(e -> {
                    TradeMessage tm = e.getMessage().getBody(TradeMessage.class);
                    e.getMessage().setHeader(KafkaConstants.KEY, tm.tradeId());
                    e.setProperty("tradeId", tm.tradeId());
                })
                .marshal("tradeJson")
                .to("kafka:{{tradehub.topics.inbound}}?enableIdempotence=true&maxBlockMs=2000");
    }
}
