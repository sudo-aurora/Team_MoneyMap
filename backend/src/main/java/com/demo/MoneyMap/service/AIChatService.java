package com.demo.MoneyMap.service;

import com.demo.MoneyMap.entity.Client;
import com.demo.MoneyMap.entity.Portfolio;
import com.demo.MoneyMap.entity.Asset;
import com.demo.MoneyMap.repository.ClientRepository;
import com.demo.MoneyMap.repository.PortfolioRepository;
import com.demo.MoneyMap.repository.AssetRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIChatService {

    private final ClientRepository clientRepository;
    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${GEMINI_API_KEY}")
    private String geminiApiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent";

    public String chatWithAI(String userMessage) {
        try {
            // Get portfolio data
            String portfolioData = getPortfolioData();
            
            // Create prompt with portfolio context
            String prompt = createPrompt(userMessage, portfolioData);
            
            // Call Gemini API
            return callGeminiAPI(prompt);
            
        } catch (Exception e) {
            log.error("Error in AI chat service", e);
            return "I'm having trouble processing your request right now. Please try again later.";
        }
    }

    private String getPortfolioData() {
        try {
            List<Client> clients = clientRepository.findAll();
            
            StringBuilder data = new StringBuilder();
            data.append("MONEYMAP PORTFOLIO DATA:\n\n");
            
            for (Client client : clients) {
                data.append("Client: ").append(client.getFirstName()).append(" ").append(client.getLastName())
                    .append(" (Email: ").append(client.getEmail()).append(")\n");
                
                List<Portfolio> portfolios = portfolioRepository.findByClientId(client.getId());
                for (Portfolio portfolio : portfolios) {
                    data.append("  Portfolio: ").append(portfolio.getName())
                        .append(" (Total Value: $").append(String.format("%.2f", portfolio.getTotalValue())).append(")\n");
                    
                    List<Asset> assets = assetRepository.findByPortfolioId(portfolio.getId());
                    for (Asset asset : assets) {
                        String assetType = asset.getClass().getSimpleName().replace("Asset", "");
                        data.append("    - ").append(asset.getName())
                            .append(" (").append(assetType).append(")")
                            .append(": ").append(asset.getQuantity()).append(" units")
                            .append(" @ $").append(String.format("%.2f", asset.getCurrentPrice()))
                            .append(" = $").append(String.format("%.2f", asset.getCurrentValue())).append("\n");
                    }
                }
                data.append("\n");
            }
            
            return data.toString();
            
        } catch (Exception e) {
            log.error("Error getting portfolio data", e);
            return "Error retrieving portfolio data";
        }
    }

    private String createPrompt(String userMessage, String portfolioData) {
        return String.format("""
            You are a helpful AI assistant for MoneyMap, a portfolio management system. 
            You have access to the following portfolio data:
            
            %s
            
            Please answer the user's question based on this data. Be helpful, professional, and concise.
            Make your responses visually appealing by:
            - Using **bold text** for important numbers, names, and key points
            - Using numbered lists for rankings
            - Using bullet points for lists
            - Adding emojis where appropriate (💰 for money, 📈 for performance, 🏆 for top performers)
            - Keeping responses well-structured and easy to read
            
            If the question cannot be answered with the provided data, say so politely.
            
            User Question: %s
            
            Provide a clear, helpful response with nice formatting:
            """, portfolioData, userMessage);
    }

    private String callGeminiAPI(String prompt) throws IOException {
        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of(
                    "parts", List.of(
                        Map.of("text", prompt)
                    )
                )
            )
        );

        String jsonBody = objectMapper.writeValueAsString(requestBody);
        
        RequestBody body = RequestBody.create(
            jsonBody, 
            MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
            .url(GEMINI_URL + "?key=" + geminiApiKey)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            
            // Extract the AI response
            JsonNode candidates = jsonResponse.get("candidates");
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode content = firstCandidate.get("content");
                if (content != null) {
                    JsonNode parts = content.get("parts");
                    if (parts != null && parts.isArray() && parts.size() > 0) {
                        JsonNode firstPart = parts.get(0);
                        JsonNode text = firstPart.get("text");
                        if (text != null) {
                            return text.asText();
                        }
                    }
                }
            }
            
            return "I couldn't generate a response. Please try again.";
        }
    }
}