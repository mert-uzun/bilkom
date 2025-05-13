package com.bilkom.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import com.bilkom.utils.DateUtils;

public class News {

    @SerializedName("id")          private Long id;
    @SerializedName("title")       private String title;
    @SerializedName("summary")     private String summary;
    @SerializedName("content")     private String content;
    @SerializedName("author")      private String author;
    @SerializedName("source")      private String source;
    @SerializedName("publishedAt") private String publishedAt;
    @SerializedName("imageUrl")    private String imageUrl;
    @SerializedName("link")        private String link;
    @SerializedName("date")        private String date;

    public News() { }

    public News(Long id, String title, String summary, String content,
                String author, String source, String publishedAt, String imageUrl) {
        this.id = id; this.title = title; this.summary = summary; this.content = content;
        this.author = author; this.source = source; this.publishedAt = publishedAt;
        this.imageUrl = imageUrl;
    }

    public Date getPublishedDate() { return DateUtils.parseApiDate(publishedAt); }
    public String getFormattedPublished() { return DateUtils.formatUserFriendlyDate(getPublishedDate()); }

    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
}
