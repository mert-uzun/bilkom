package com.bilkom.dto;

/**
 * NewsDto is a Data Transfer Object (DTO) that represents news information.
 * It contains the title and link of the news article.
 * 
 * @author Elif Bozkurt
 * @version 1.0
 */
public class NewsDto {
    private String title;
    private String link;

    public NewsDto() {
    }

    public NewsDto(String title, String link) {
        this.title = title;
        this.link = link;
    }

    public String getTitle() { return title;}
    public void setTitle(String title) { this.title = title; }
    
    public String getLink() { return link; }

    public void setLink(String link) { this.link = link; }
}
