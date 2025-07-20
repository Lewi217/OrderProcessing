package com.sanifu.order_processing.config;

import lombok.Data;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ocr")
@Data
public class OcrConfig {
    private String dataPath;
    private String language;

    @Bean
    public Tesseract tesseract() {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(dataPath);
        tesseract.setLanguage(language);
        tesseract.setPageSegMode(1);
        tesseract.setOcrEngineMode(1);
        return tesseract;
    }
}
