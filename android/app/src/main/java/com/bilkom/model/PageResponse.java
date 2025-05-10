package com.bilkom.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Generic class to handle paginated responses from the API
 * We didn't really need this class for our CS102 project demo, but I implemented it for future use
 * 
 * @param <T> Type of content being paginated
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-05-09
 */
public class PageResponse<T> {
    @SerializedName("content")
    private List<T> content;
    
    @SerializedName("totalPages")
    private int totalPages;
    
    @SerializedName("totalElements")
    private long totalElements;
    
    @SerializedName("number")
    private int number;
    
    @SerializedName("size")
    private int size;
    
    @SerializedName("first")
    private boolean first;
    
    @SerializedName("last")
    private boolean last;
    
    @SerializedName("empty")
    private boolean empty;

    public PageResponse() {
    }

    // GETTERS AND SETTERS

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
} 