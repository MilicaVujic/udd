package com.example.udd_security_incidents.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Dozvoljava sve rute
                .allowedOrigins("http://localhost:4200") // Dozvoljava samo zahtev sa localhost:4200
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Dozvoljava samo ove HTTP metode
                .allowedHeaders("*")
                .exposedHeaders("Authorization");     }
}
