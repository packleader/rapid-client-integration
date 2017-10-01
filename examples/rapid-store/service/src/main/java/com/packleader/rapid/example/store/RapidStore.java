package com.packleader.rapid.example.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@SpringBootApplication
@EnableSwagger2
@ComponentScan({"com/packleader/rapid/example/store"})
public class RapidStore {

    public static void main(String[] args) {
        SpringApplication.run(RapidStore.class, args);
    }

    @Bean
    public Docket storeApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("v1")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/v1/.*"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("RAPID Store Example")
                .description("Sample service with RAPID client integration")
                .license("Apache License Version 2.0")
                .version("1.0")
                .build();
    }
}
