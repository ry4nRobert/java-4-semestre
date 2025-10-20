package com.uniruy.appuser;

import org.springframework.lang.NonNull; 

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override

    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        
        String resourceHandler = "/uploads/**";
        
        String resourceLocation = "file:./uploads/";
        
        registry.addResourceHandler(resourceHandler)
                .addResourceLocations(resourceLocation);
    }
}