package com.demo.MoneyMap.controller;

import com.demo.MoneyMap.service.AIChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "AI Chat", description = "AI-powered portfolio assistant")
public class AIChatController {

    private final AIChatService aiChatService;

    @PostMapping("/chat")
    @Operation(summary = "Chat with AI assistant", description = "Ask questions about portfolios, clients, and assets")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        try {
            String userMessage = request.get("message");
            if (userMessage == null || userMessage.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Message cannot be empty"
                ));
            }

            String aiResponse = aiChatService.chatWithAI(userMessage);
            
            return ResponseEntity.ok(Map.of(
                "response", aiResponse
            ));
            
        } catch (Exception e) {
            log.error("Error in AI chat endpoint", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to process your request. Please try again."
            ));
        }
    }

    @GetMapping("/status")
    @Operation(summary = "Check AI service status", description = "Check if the AI service is running")
    public ResponseEntity<Map<String, String>> status() {
        return ResponseEntity.ok(Map.of(
            "status", "AI Chat Service is running",
            "version", "1.0.0"
        ));
    }
}