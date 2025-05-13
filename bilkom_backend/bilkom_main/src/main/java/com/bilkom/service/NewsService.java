package com.bilkom.service;

import com.bilkom.dto.NewsDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for fetching news content from Bilkent University sources.
 * 
 * @author Mert Uzun
 * @version 1.1
 */
@Service
public class NewsService {
    private static final Logger log = LoggerFactory.getLogger(NewsService.class);
    private static final String BILKENT_NEWS_URL = "https://bilkentnews.bilkent.edu.tr/";
    
    /**
     * Fetches news from Bilkent News website.
     * 
     * @return List of NewsDto objects containing news title and link
     * 
     * @author Mert Uzun
     * @version 1.0
     */
    public List<NewsDto> fetchFromBilkentNews() {
        List<NewsDto> result = new ArrayList<>();
        
        try {
            // Connect to the Bilkent News website and get the document
            Document doc = Jsoup.connect(BILKENT_NEWS_URL).get();
            
            // Select all news article titles with links
            Elements newsLinks = doc.select("h3.entry-title a");
            
            // Process each news item
            for (Element link : newsLinks) {
                String title = link.text();
                String url = link.absUrl("href");
                
                // Skip invalid entries
                if (title == null || title.isEmpty() || url == null || url.isEmpty()) {
                    continue;
                }
                
                // Add to result list
                result.add(new NewsDto(title, url));
            }
            
            log.info("Fetched {} news items from Bilkent News", result.size());
        } catch (IOException e) {
            log.error("Error fetching news from Bilkent News: {}", e.getMessage(), e);
        }
        
        return result;
    }
}
