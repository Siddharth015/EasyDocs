package com.easydocs.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class GitHubService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String GITHUB_API_BASE = "https://api.github.com/repos/";
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(
        ".java", ".py", ".js", ".ts", ".cpp", ".html", ".css", ".md"
    );

    @Value("${github.api.token}")
    private String githubApiToken;

    public List<Map<String, String>> getRepositoryFiles(String repoUrl) {
        if (!repoUrl.startsWith("https://github.com/")) {
            throw new IllegalArgumentException("Invalid GitHub URL. Must start with: https://github.com/");
        }

        String[] pathSegments = repoUrl.replace("https://github.com/", "").split("/");
        if (pathSegments.length < 2) {
            throw new IllegalArgumentException("URL must include owner and repository name.");
        }

        String owner = UriUtils.encodePath(pathSegments[0], StandardCharsets.UTF_8);
        String repo = UriUtils.encodePath(
            pathSegments[1].replace(".git", ""), 
            StandardCharsets.UTF_8
        );

        // Start with the root contents API URL
        String apiUrl = String.format("%s%s/%s/contents", GITHUB_API_BASE, owner, repo);
        List<Map<String, String>> files = new ArrayList<>();
        fetchFilesRecursively(apiUrl, files);
        return files;
    }

    private void fetchFilesRecursively(String apiUrl, List<Map<String, String>> files) {
        HttpHeaders headers = createGitHubHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("User-Agent", "EasyDocs");
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl, 
                HttpMethod.GET, 
                new HttpEntity<>(headers), 
                String.class
            );
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    String type = node.get("type").asText();
                    String name = node.get("name").asText();
                    if ("file".equals(type)) {
                        // Skip files that look like test files
                        if (name.toLowerCase().contains("test")) {
                            continue;
                        }
                        // Only include supported file types
                        if (isSupportedFile(name)) {
                            String downloadUrl = node.get("download_url").asText();
                            files.add(Map.of(
                                "name", name,
                                "url", downloadUrl
                            ));
                        }
                    } else if ("dir".equals(type)) {
                        // Recurse into directories
                        String dirUrl = node.get("url").asText();
                        fetchFilesRecursively(dirUrl, files);
                    }
                }
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("GitHub API error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching repository files: " + e.getMessage());
        }
    }

    public boolean isSupportedFile(String fileName) {
        return SUPPORTED_EXTENSIONS.stream().anyMatch(fileName::endsWith);
    }

    public String fetchFileContent(String fileUrl) {
        try {
            HttpHeaders headers = createGitHubHeaders();
            headers.setAccept(List.of(MediaType.TEXT_PLAIN));
            headers.set("User-Agent", "EasyDocs");
            ResponseEntity<String> response = restTemplate.exchange(
                fileUrl, 
                HttpMethod.GET, 
                new HttpEntity<>(headers), 
                String.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to fetch file: " + e.getStatusCode());
        }
    }
     private HttpHeaders createGitHubHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("User-Agent", "EasyDocs");
        if (githubApiToken != null && !githubApiToken.isEmpty()) {
            headers.set("Authorization", "Bearer " + githubApiToken);
        }
        return headers;
    }
}