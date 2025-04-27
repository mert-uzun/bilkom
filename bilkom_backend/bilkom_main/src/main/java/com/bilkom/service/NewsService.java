package com.bilkom.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import com.bilkom.dto.NewsDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsService {

    public List<NewsDto> fetchFromBilkentNewsWithJsoup() {
        List<NewsDto> newsList = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("https://bilkentnews.bilkent.edu.tr/").get();
    
            Elements articles = doc.select("h3.entry-title a"); 
    
            for (Element article : articles) {
                String title = article.text();
                String link = article.absUrl("href");
    
                if (title != null && !title.isEmpty() && link != null && !link.isEmpty()) {
                    newsList.add(new NewsDto(title, link));
                }
            }
        } catch (Exception e) {
            System.out.println("\n\n");
            e.printStackTrace();
            System.out.println("\n\n");
        }
        return newsList;
    }    
}
