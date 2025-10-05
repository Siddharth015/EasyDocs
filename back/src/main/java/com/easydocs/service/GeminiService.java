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

    // Using a more recent and powerful model
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    public String generateProjectDocumentation(String readme, List<FileData> files, String techStack, String githubToken) {
        if (geminiApiKey == null || geminiApiKey.isEmpty()) {
            throw new RuntimeException("Gemini API Key is missing. Check application-secret.properties");
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
        
        // --- PROMPT ENGINEERING ---
        prompt.append("You are an expert senior software architect creating documentation for a developer audience.\n");
        prompt.append("Your task is to generate a comprehensive, production-level README.md file in GitHub Flavored Markdown.\n\n");
        prompt.append("## INSTRUCTIONS:\n");
        prompt.append("1.  *Project Overview:* Start with a concise, high-level summary of the project's purpose.\n");
        
        // --- NEW INSTRUCTION FOR ARCHITECTURE DIAGRAM ---
        prompt.append("2.  *Architecture Diagram:* Create a high-level architecture diagram using Mermaid.js syntax. The diagram should show the main components (e.g., Frontend, Backend, APIs, Databases) and their interactions. Enclose the diagram in a mermaid code fence.\n");
        
        prompt.append("3.  **Module Breakdown:** Provide detailed explanations for each important file and module.\n");
        
        // --- NEW CONDITIONAL LOGIC FOR INSTALLATION STEPS ---
        boolean hasPackageJson = files.stream().anyMatch(file -> "package.json".equalsIgnoreCase(file.getName()));
        if (hasPackageJson) {
            prompt.append("4.  **Getting Started:** A `package.json` was found. Based on its contents, generate a 'Getting Started' section with installation (`npm install`) and running (`npm run dev`) instructions.\n");
        }
        
        prompt.append("5.  **Formatting Rules:**\n");
        prompt.append("    - The entire output must be pure Markdown content only.\n");
        prompt.append("    - Do not wrap the entire response in a code fence ().\n");
        prompt.append("    - Do not include any manual HTML anchor tags like <a name=\"...\">.\n\n");

        prompt.append("## REPOSITORY CONTEXT:\n");
        prompt.append("The primary tech stack is: ").append(techStack).append(".\n\n");
        prompt.append("### Existing README (if any):\n").append(readme).append("\n\n");

        for (FileData file : files) {
            prompt.append("### File Path: ").append(file.getName()).append("\n");
            prompt.append("\n").append(file.getContent()).append("\n\n\n");
        }
        
        return prompt.toString();
    }
    
    private String extractGeneratedContent(String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            System.out.println("Gemini API Response: " + rootNode.toPrettyString());
            // This structure is safer and checks for the existence of nodes
            JsonNode candidates = rootNode.path("candidates");
            if (candidates.isArray() && !candidates.isEmpty()) {
                JsonNode parts = candidates.get(0).path("content").path("parts");
                if (parts.isArray() && !parts.isEmpty()) {
                    return parts.get(0).path("text").asText("No documentation generated.");
                }
            }
            // Add a fallback for rate limiting or other errors
            JsonNode error = rootNode.path("error");
            if (!error.isMissingNode()) {
                 return "Error from Gemini API: " + error.path("message").asText("Unknown error.");
            }
            return "No documentation generated or unexpected response structure.";
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Gemini response: " + e.getMessage());
        }
    }
}