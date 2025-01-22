package com.lgs.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Permite para todos os endpoints
                .allowedOrigins("http://127.0.0.1:5500") // Origem permitida
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos permitidos
                .allowedHeaders("Authorization", "Content-Type") // Cabeçalhos permitidos
                .allowCredentials(true); // Permite credenciais (como cookies e tokens)
    }
}