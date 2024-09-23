package ru.it.vs.data_collection_bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties("bot")
public class BotConfig {
    private String token;
    private String name;
}
