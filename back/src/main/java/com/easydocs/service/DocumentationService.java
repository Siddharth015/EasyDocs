package com.easydocs.service;

import org.springframework.stereotype.Service;
import com.easydocs.dto.FileData;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentationService {
    private final GitHubService gitHubService;
    private final GeminiService geminiService;

    public DocumentationService(GitHubService gitHubService, GeminiService geminiService) {
        this.gitHubService = gitHubService;
        this.geminiService = geminiService;
    }

    public String generateProjectDocumentation(String repoUrl, String githubToken) {
        // Fetch all files recursively from GitHub
        List<Map<String, String>> files = gitHubService.getRepositoryFiles(repoUrl, githubToken);

        // Extract README content (if available)
        String readmeContent = files.stream()
            .filter(file -> file.get("name").equalsIgnoreCase("README.md"))
            .findFirst()
            .map(readme -> gitHubService.fetchFileContent(readme.get("url"), githubToken))
            .orElse("No README found.");

        // Extract other code files (skip README)
        List<FileData> fileContents = files.stream()
            .filter(file -> !file.get("name").equalsIgnoreCase("README.md"))
            .map(file -> new FileData(
                file.get("name"),
                file.get("url"),
                gitHubService.fetchFileContent(file.get("url"), githubToken)
            ))
            .collect(Collectors.toList());

        // Detect predominant tech stack based on file extensions
        String techStack = detectTechStack(fileContents);

        // Generate documentation using Gemini AI with tech stack info
        String documentation = geminiService.generateProjectDocumentation(readmeContent, fileContents, techStack, githubToken);

        return documentation;
    }

    private String detectTechStack(List<FileData> fileContents) {
        Map<String, Integer> counts = new HashMap<>();
        // Count occurrences of supported extensions
        for (FileData file : fileContents) {
            String name = file.getName().toLowerCase();
            if (name.endsWith(".java")) {
                counts.merge("Java", 1, Integer::sum);
            } else if (name.endsWith(".py")) {
                counts.merge("Python", 1, Integer::sum);
            } else if (name.endsWith(".js") || name.endsWith(".ts")) {
                counts.merge("JavaScript/TypeScript", 1, Integer::sum);
            } else if (name.endsWith(".cpp")) {
                counts.merge("C++", 1, Integer::sum);
            } else if (name.endsWith(".html") || name.endsWith(".css")) {
                counts.merge("Web (HTML/CSS)", 1, Integer::sum);
            }
        }
        // Find the tech stack with the highest count
        return counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");
    }
}
