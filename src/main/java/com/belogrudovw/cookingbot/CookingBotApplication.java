package com.belogrudovw.cookingbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class CookingBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(CookingBotApplication.class, args);
    }

}