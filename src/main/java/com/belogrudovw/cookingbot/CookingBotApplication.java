package com.belogrudovw.cookingbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CookingBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(CookingBotApplication.class, args);
    }

}
