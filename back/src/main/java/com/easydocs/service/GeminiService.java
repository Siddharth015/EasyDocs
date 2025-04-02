package com.easydocs.service;

import com.easydocs.dto.FileData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class GeminiService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key}") 
    private String geminiApiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public String generateProjectDocumentation(String readme, List<FileData> files, String techStack, String githubToken) {
        if (geminiApiKey == null || geminiApiKey.isEmpty()) {
            throw new RuntimeException("Gemini API Key is missing. Check application.properties");
        }

        String prompt = buildPrompt(readme, files, techStack);
        Map<String, Object> requestBody = Map.of(
            "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        String requestUrl = GEMINI_URL + "?key=" + geminiApiKey;

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return extractGeneratedContent(response.getBody());
            } else {
                throw new RuntimeException("Gemini API error: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Gemini API request failed: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    private String buildPrompt(String readme, List<FileData> files, String techStack) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a senior software architect. Generate detailed, production‑level documentation for the following repository. ");
        prompt.append("Focus on the primary tech stack: ").append(techStack).append(". ");
        prompt.append("Include a comprehensive project overview, detailed explanations for each module, usage examples, and error handling information.\n\n");
        prompt.append("### README:\n").append(readme).append("\n\n");
        for (FileData file : files) {
            prompt.append("### File: ").append(file.getName()).append("\n");
            prompt.append(file.getContent()).append("\n\n");
        }
        return prompt.toString();
    }
    
    private String extractGeneratedContent(String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            System.out.println("Gemini API Response: " + rootNode.toPrettyString());
            return rootNode
                .path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText("No documentation generated.");
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Gemini response: " + e.getMessage());
        }
    }
}
