package com.sanchit.settlement;

import org.springframework.boot.SpringApplication;

public class TestTradeHubApplication {

	public static void main(String[] args) {
		SpringApplication.from(TradeHubApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
