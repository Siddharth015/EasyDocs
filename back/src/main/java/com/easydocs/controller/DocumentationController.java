package com.easydocs.controller;

import com.easydocs.service.DocumentationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class DocumentationController {
    private final DocumentationService documentationService;

    public DocumentationController(DocumentationService documentationService) {
        this.documentationService = documentationService;
    }

    @PostMapping("/generate-documentation")
    public ResponseEntity<?> generateDocs(@RequestBody Map<String, String> request) {
        try {
            String repoUrl = validateRequest(request);
            
            String documentation = documentationService.generateProjectDocumentation(repoUrl);
            
            return ResponseEntity.ok(Map.of("documentation", documentation));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
                    return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Documentation generation failed: " + e.getMessage()));
        }
    }

    private String validateRequest(Map<String, String> request) {
        String repoUrl = request.get("repo_url");
        if (repoUrl == null || !repoUrl.startsWith("https://github.com/")) {
            throw new IllegalArgumentException("Valid GitHub URL required");
        }
        return repoUrl;
    }
}