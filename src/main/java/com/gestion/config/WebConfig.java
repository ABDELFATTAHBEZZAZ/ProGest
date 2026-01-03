package com.gestion.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.audio-path}")
    private String audioPath;

    @Value("${app.upload.image-path}")
    private String imagePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded audio files
        registry.addResourceHandler("/uploads/audio/**")
                .addResourceLocations("file:" + audioPath);

        // Serve uploaded image files
        registry.addResourceHandler("/uploads/image/**")
                .addResourceLocations("file:" + imagePath);

        // Alternative path for images
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:" + imagePath);
    }
}
