package com.easydocs.dto;

public class FileData {
    private String name;
    private String url;
    private String content;

    public FileData(String name, String url, String content) {
        this.name = name;
        this.url = url;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }
}
