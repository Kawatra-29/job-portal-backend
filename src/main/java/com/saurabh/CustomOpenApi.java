package com.saurabh;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class CustomOpenApi {
	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI()
				.addServersItem(new Server().url("http://localhost:8080"))
				.addServersItem(new Server().url("https://job-portal-backend-production-1bc7.up.railway.app"));
	}
}