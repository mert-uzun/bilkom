package com.bilkom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bilkom.dto.NewsDto;
import com.bilkom.service.NewsService;

import java.util.List;

/**
 * NewsController is responsible for handling HTTP requests related to news.
 * It provides an endpoint for fetching news titles and links from Bilkent University.
 *
 * @author Elif Bozkurt, Mert Uzun
 * @version 1.1
 */
@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    /**
     * Fetches news titles and links from Bilkent University.
     * 
     * @return List of NewsDto objects containing news titles and links
     * 
     * @author Elif Bozkurt, Mert Uzun
     * @version 1.1
     */
    @GetMapping
    public List<NewsDto> getBilkentNews() {
        return newsService.fetchFromBilkentNews();
    }
}
