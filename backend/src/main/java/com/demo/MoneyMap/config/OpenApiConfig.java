package com.demo.MoneyMap.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for OpenAPI/Swagger documentation.
 * Provides comprehensive API documentation for the MoneyMap Portfolio Manager.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8181}")
    private String serverPort;

    @Bean
    public OpenAPI moneyMapOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:" + serverPort);
        devServer.setDescription("Development server for MoneyMap Portfolio Manager");

        Contact contact = new Contact();
        contact.setName("MoneyMap Support Team");
        contact.setEmail("support@moneymap.com");
        contact.setUrl("https://moneymap.com");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("MoneyMap Portfolio Manager API")
                .version("1.0.0")
                .description("""
                        ## Overview
                        MoneyMap is a comprehensive Portfolio Management REST API designed for asset managers 
                        to manage client portfolios efficiently.
                        
                        ## Features
                        - **Client Management**: Create and manage up to 30-35 clients
                        - **Portfolio Management**: Multiple portfolios per client
                        - **Asset Types**: Support for Gold, Stocks, Mutual Funds, and Cryptocurrencies
                        - **Transaction Tracking**: Record buys, sells, dividends, and transfers
                        - **Value Tracking**: Automatic portfolio value calculation and profit/loss tracking
                        
                        ## API Versioning
                        All endpoints are prefixed with `/api/v1/` for versioning.
                        
                        ## Authentication
                        Currently, the API does not require authentication (single user assumed).
                        
                        ## Error Handling
                        All errors return a consistent JSON structure with:
                        - `success`: false
                        - `message`: Human-readable error message
                        - `error`: Object containing error code and details
                        - `timestamp`: When the error occurred
                        """)
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
