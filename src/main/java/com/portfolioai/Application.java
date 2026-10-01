package com.portfolioai;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "portfolioai",
                version = "0.1",
                description = "RAG resume assistant API consumed by the Portfolio web app. "
                        + "Health probes use GET /health (Micronaut Management, not listed below)."
        )
)
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}