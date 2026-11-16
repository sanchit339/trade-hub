package com.sanchit.settlement.routes;

import com.sanchit.settlement.model.TradeCsvRecord;
import com.sanchit.settlement.validation.TradeValidator;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CsvIngestRoute extends RouteBuilder {
    private static final Logger log = LoggerFactory.getLogger(CsvIngestRoute.class);
    private static final String STATS = "fileStats";
    private static final String ROW_REJECTED = "rowRejected";

    private final TradeValidator tradeValidator;

    public CsvIngestRoute(TradeValidator tradeValidator){
        this.tradeValidator = tradeValidator;
    }

    @Override
    public void configure() throws Exception {
        // Here - readLock = Changed - a file consumer read-lock strategy used to ensure a file is fully written before it is picked up for processing
        from("file:{{tradehub.inbox}}?includeExt=csv&charset=UTF-8&readLock=changed" + "&move=.processed/${file:name}&moveFailed=.error/${file:name}")
                .routeId("csv-ingest")
                .log("Received file ${file:name}")
                .process(e -> e.setProperty(STATS, new FileStats()))
                .split(body().tokenize("\n")).streaming() // .stopOnException()
                    .choice()
                        .when(e -> e.getProperty(Exchange.SPLIT_INDEX, Integer.class) > 0 && !String.valueOf(e.getMessage().getBody()).isBlank())
                            .to("direct:csv-row")
                    .end()
                .end()
                .process(e -> {
                        FileStats s = e.getProperty(STATS, FileStats.class);
                        log.info("File {} done: {} accepted, {} rejected", e.getMessage().getHeader(Exchange.FILE_NAME), s.getAccepted(), s.getRejected());
                });

        from("direct:csv-row")
                .routeId("csv-row")
                .to("direct:csv-parse")
                .choice()
                    .when(e -> e.getProperty(ROW_REJECTED) == null)
                        .to("direct:publish-trade")
                        .process(e -> e.getProperty(STATS, FileStats.class).accepted())
                .end();

        from("direct:csv-parse")
                .routeId("csv-parse")
                .onException(Exception.class) //  // ← catches exceptions from the 3 lines below [line 65 - 67]
                    .handled(true)
                    .process(e -> {
                        Exception ex = e.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                        e.setProperty(ROW_REJECTED, true); // marks row as Bad
                        e.getProperty(STATS, FileStats.class).rejected(); // counts

                        log.warn("Rejected row {} in {}, {}",
                                e.getProperty(Exchange.SPLIT_INDEX, Integer.class) + 1,
                                e.getMessage().getHeader(Exchange.FILE_NAME),ex.getMessage());
                    })
                .end()
                .unmarshal().bindy(BindyType.Csv, TradeCsvRecord.class)
                .process(e -> e.getMessage().setBody(e.getMessage().getBody(TradeCsvRecord.class).toMessage().withSource("CSV"))) // setting the source as CSV
                .bean(tradeValidator, "validate");
    }
}
