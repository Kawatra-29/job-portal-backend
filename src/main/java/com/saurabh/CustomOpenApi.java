package com.saurabh;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class CustomOpenApi {

	@Value("${app.backend.url:}")
	private String backendUrl;

	@Bean
	OpenAPI customOpenAPI() {
		OpenAPI openAPI = new OpenAPI()
				.addServersItem(new Server().url("/").description("Default Server"))
				.addServersItem(new Server().url("http://localhost:8080").description("Local Development"));

		if (backendUrl != null && !backendUrl.isBlank()) {
			openAPI.addServersItem(new Server().url(backendUrl).description("Configured Environment"));
		}

		return openAPI;
	}
}
