package com.sanchit.settlement.config;

import com.sanchit.settlement.model.TradeMessage;
import org.apache.camel.component.jackson3.JacksonDataFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class JsonConfig {
    @Bean(name = "tradeJson")
    public JacksonDataFormat tradeJson(){
        JsonMapper mapper = JsonMapper.builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // need to check later
                .build();
        return new JacksonDataFormat(mapper, TradeMessage.class); // Trade message has the all the properties which needs to be mapped
    }
}
