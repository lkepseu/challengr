package com.challengr.submission.config;

import org.springframework.context.annotation.Configuration;                     // <-- manquait
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;      // <-- manquait
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry; // <-- manquait

@Configuration
public class StaticUploadConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry r) {
        r.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/uploads/");
    }
}
