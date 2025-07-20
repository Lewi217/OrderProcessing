package com.sanifu.order_processing.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
@ConfigurationProperties(prefix = "whatsapp")
@Data
public class WhatsAppConfig {
    private String apiUrl;
    private String apiToken;
    private String webhookVerifyToken;
    private String phoneNumberId;

    @Bean
    public Retrofit whatsappRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(apiUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }
}
