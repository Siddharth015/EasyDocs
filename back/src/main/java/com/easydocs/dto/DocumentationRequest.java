package com.easydocs.dto;

import java.util.List;

public class DocumentationRequest {
    private String readme;
    private List<FileData> otherFiles; // Ensure this matches the JSON structure

    public String getReadme() {
        return readme;
    }

    public void setReadme(String readme) {
        this.readme = readme;
    }

    public List<FileData> getOtherFiles() {
        return otherFiles;
    }

    public void setOtherFiles(List<FileData> otherFiles) {
        this.otherFiles = otherFiles;
    }

    @Override
    public String toString() {
        return "DocumentationRequest{" +
                "readme='" + readme + '\'' +
                ", otherFiles=" + otherFiles +
                '}';
    }
}
