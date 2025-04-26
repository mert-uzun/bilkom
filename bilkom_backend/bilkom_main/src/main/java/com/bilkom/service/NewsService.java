package com.bilkom.service;

import com.bilkom.dto.NewsDto;
import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsService {

    public List<NewsDto> fetchFromBilkentNews() {
        List<NewsDto> newsList = new ArrayList<>();
        try {
            URL feedUrl = new URL("https://bilkentnews.bilkent.edu.tr/feed/");
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            for (SyndEntry entry : feed.getEntries()) {
                String title = entry.getTitle();
                String link = entry.getLink();
                String summary = entry.getDescription() != null ? entry.getDescription().getValue() : "";
                newsList.add(new NewsDto(title, summary, link));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return newsList;
    }
}
