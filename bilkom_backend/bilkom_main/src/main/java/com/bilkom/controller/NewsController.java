package com.bilkom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bilkom.dto.NewsDto;
import com.bilkom.service.NewsService;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/bilkent")
    public List<NewsDto> getBilkentNews() {
        return newsService.fetchFromBilkentNewsWithJsoup();
    }
}
