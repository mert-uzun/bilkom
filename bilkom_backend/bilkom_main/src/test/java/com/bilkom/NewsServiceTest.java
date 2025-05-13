package com.bilkom;

import com.bilkom.dto.NewsDto;
import com.bilkom.service.NewsService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for NewsService functionality which fetches news titles and links from Bilkent News.
 * 
 * @author Mert Uzun
 * @version 1.1
 */
@SpringBootTest
@ActiveProfiles("test")
public class NewsServiceTest {

    @Autowired
    private NewsService newsService;
    
    private Document mockDocument;
    private Connection mockConnection;
    
    @BeforeEach
    public void setUp() {
        // Setup mock document and elements
        mockDocument = mock(Document.class);
        mockConnection = mock(Connection.class);
        
        // We'll create mock Elements and Element for testing
        Element link1 = mock(Element.class);
        when(link1.text()).thenReturn("Test News Article 1");
        when(link1.absUrl("href")).thenReturn("https://bilkentnews.bilkent.edu.tr/test-article-1");
        
        Element link2 = mock(Element.class);
        when(link2.text()).thenReturn("Test News Article 2");
        when(link2.absUrl("href")).thenReturn("https://bilkentnews.bilkent.edu.tr/test-article-2");
        
        Element link3 = mock(Element.class);
        when(link3.text()).thenReturn("Test News Article 3");
        when(link3.absUrl("href")).thenReturn("https://bilkentnews.bilkent.edu.tr/test-article-3");
        
        // Create Elements for the links
        Elements links = new Elements();
        links.add(link1);
        links.add(link2);
        links.add(link3);
        
        // Setup document response
        when(mockDocument.select("h3.entry-title a")).thenReturn(links);
    }
    
    @Test
    public void testFetchNewsFromBilkent() throws IOException {
        // We need to mock static method Jsoup.connect
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            // Setup the static mock
            jsoupMock.when(() -> Jsoup.connect(anyString())).thenReturn(mockConnection);
            when(mockConnection.get()).thenReturn(mockDocument);
            
            // Call the service method
            List<NewsDto> newsList = newsService.fetchFromBilkentNews();
            
            // Verify the mock was used
            jsoupMock.verify(() -> Jsoup.connect("https://bilkentnews.bilkent.edu.tr/"));
            
            // Verify results
            assertNotNull(newsList, "News list should not be null");
            assertEquals(3, newsList.size(), "Should return 3 news articles");
            
            // Check content
            assertEquals("Test News Article 1", newsList.get(0).getTitle(), "First article title should match");
            assertEquals("https://bilkentnews.bilkent.edu.tr/test-article-1", newsList.get(0).getLink(), "First article link should match");
            
            assertEquals("Test News Article 2", newsList.get(1).getTitle(), "Second article title should match");
            assertEquals("https://bilkentnews.bilkent.edu.tr/test-article-2", newsList.get(1).getLink(), "Second article link should match");
            
            assertEquals("Test News Article 3", newsList.get(2).getTitle(), "Third article title should match");
            assertEquals("https://bilkentnews.bilkent.edu.tr/test-article-3", newsList.get(2).getLink(), "Third article link should match");
        }
    }
    
    @Test
    public void testHandleEmptyNewsResults() throws IOException {
        // Mock an empty selection
        when(mockDocument.select("h3.entry-title a")).thenReturn(new Elements());
        
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            // Setup the static mock
            jsoupMock.when(() -> Jsoup.connect(anyString())).thenReturn(mockConnection);
            when(mockConnection.get()).thenReturn(mockDocument);
            
            // Call the service method
            List<NewsDto> newsList = newsService.fetchFromBilkentNews();
            
            // Verify results
            assertNotNull(newsList, "News list should not be null even if empty");
            assertTrue(newsList.isEmpty(), "News list should be empty");
        }
    }
    
    @Test
    public void testHandleConnectionError() throws IOException {
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            // Setup the static mock to throw exception
            jsoupMock.when(() -> Jsoup.connect(anyString())).thenReturn(mockConnection);
            when(mockConnection.get()).thenThrow(new IOException("Connection error"));
            
            // Call the service method
            List<NewsDto> newsList = newsService.fetchFromBilkentNews();
            
            // Verify error handling
            assertNotNull(newsList, "News list should not be null on error");
            assertTrue(newsList.isEmpty(), "News list should be empty on error");
        }
    }
    
    @Test
    public void testHandleInvalidNews() throws IOException {
        // Create a link with invalid data
        Element invalidLink = mock(Element.class);
        when(invalidLink.text()).thenReturn(""); // Empty title
        when(invalidLink.absUrl("href")).thenReturn(null); // Null link
        
        // Add valid and invalid links to the elements
        Elements mixedLinks = new Elements();
        
        Element validLink = mock(Element.class);
        when(validLink.text()).thenReturn("Valid Article");
        when(validLink.absUrl("href")).thenReturn("https://bilkentnews.bilkent.edu.tr/valid-article");
        
        mixedLinks.add(invalidLink);
        mixedLinks.add(validLink);
        
        // Setup document response with mixed links
        when(mockDocument.select("h3.entry-title a")).thenReturn(mixedLinks);
        
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            // Setup the static mock
            jsoupMock.when(() -> Jsoup.connect(anyString())).thenReturn(mockConnection);
            when(mockConnection.get()).thenReturn(mockDocument);
            
            // Call the service method
            List<NewsDto> newsList = newsService.fetchFromBilkentNews();
            
            // Verify results - it should only include the valid news
            assertNotNull(newsList, "News list should not be null");
            assertEquals(1, newsList.size(), "Should return only valid news articles");
            assertEquals("Valid Article", newsList.get(0).getTitle(), "Valid article title should match");
        }
    }
} 