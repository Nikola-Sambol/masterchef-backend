package com.mev.recipeapp.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String UPLOAD_DIR = System.getProperty("java.io.tmpdir") + "/uploads/";

    @PostConstruct
    public void init() {
        File images = new File(UPLOAD_DIR + "images");
        File videos = new File(UPLOAD_DIR + "videos");
        if (!images.exists()) images.mkdirs();
        if (!videos.exists()) videos.mkdirs();
        System.out.println("Uploads folder: " + UPLOAD_DIR);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadsPath = "file:" + UPLOAD_DIR;
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadsPath);
        System.out.println("Serving uploads from: " + uploadsPath);
    }
}

