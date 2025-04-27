package com.Qiydaardev.systemcontrol.config;

import io.jsonwebtoken.lang.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer webMvcConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("GET","POST","PUT","DELETE")
                        .allowedOrigins("http://localhost:5173",  "https://systemcontroller2-n819-git-main-qiydaar23s-projects.vercel.app")
                        .allowCredentials(true)
                        .allowedHeaders("*");
            }
        };
    }
}
