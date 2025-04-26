package com.bilkom.service;

import com.bilkom.dto.NewsDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsService {
public List<NewsDto> fetchFromGazeteBilkent() {
    List<NewsDto> newsList = new ArrayList<>();
    try {
        Document doc = Jsoup.connect("https://gazetebilkent.com/").get();
        
        // Find article cards (you may adjust the CSS selectors based on actual HTML)
        Elements articles = doc.select("article");

        for (Element article : articles) {
            // Extract title
            Element titleElement = article.selectFirst("h2.entry-title a");
            String title = titleElement != null ? titleElement.text() : "No Title";
            String link = titleElement != null ? titleElement.attr("href") : "#";

            // Extract summary (if available)
            Element summaryElement = article.selectFirst("div.entry-summary p");
            String summary = summaryElement != null ? summaryElement.text() : "";

            newsList.add(new NewsDto(title, summary, link));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return newsList;
}
}
