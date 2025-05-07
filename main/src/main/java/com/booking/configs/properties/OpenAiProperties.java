package com.booking.configs.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("openai.api")
@Getter
@Setter
public class OpenAiProperties {
    private String model;
    private String url;
    private String key;
}
