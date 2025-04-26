package com.bilkom.dto;

public class NewsDto {
    private String title;
    private String summary;
    private String link;

    public NewsDto() {
    }

    public NewsDto(String title, String summary, String link) {
        this.title = title;
        this.summary = summary;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
