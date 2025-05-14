package com.bilkom.repository;

import android.content.Context;

import com.bilkom.model.Event;
import com.bilkom.model.EventRequest;
import com.bilkom.model.PageResponse;
import com.bilkom.network.ApiService;
import com.bilkom.network.RetrofitClient;
import com.bilkom.utils.ApiErrorHandler;
import com.bilkom.utils.CacheManager;
import com.bilkom.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Repository class for Event-related operations
 * Demonstrates how to use pagination and caching
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-05-09
 */
public class EventRepository {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final String CACHE_KEY_EVENTS = "events_all";
    private static final String CACHE_KEY_EVENTS_PAGE = "events_page_";
    
    private final ApiService apiService;
    private final CacheManager cacheManager;
    private final SessionManager sessionManager;
    
    /**
     * Constructor for EventRepository
     * 
     * @param context Activity context
     * 
     * @author Mert Uzun
     * @version 1.0
     * @since 2025-05-09
     */
    public EventRepository(Context context) {
        apiService = RetrofitClient.getInstance().getApiService();
        cacheManager = CacheManager.getInstance(context);
        sessionManager = SessionManager.getInstance(context);
    }
    
    /**
     * Get all events with pagination
     * 
     * @param context Activity context
     * @param page Page number (0-based)
     * @param pageSize Number of items per page
     * @param onSuccess Success callback
     * @param onError Error callback
     * 
     * @author Mert Uzun
     * @version 1.0
     * @since 2025-05-09
     */
    public void getEvents(
            Context context, 
            int page, 
            int pageSize, 
            ApiErrorHandler.OnSuccess<PageResponse<Event>> onSuccess,
            ApiErrorHandler.OnError onError) {
        
        // Try to get from cache first
        String cacheKey = CACHE_KEY_EVENTS_PAGE + page + "_" + pageSize;
        PageResponse<Event> cachedEvents = cacheManager.get(cacheKey, PageResponse.class);
        
        if (cachedEvents != null) {
            // Return cached data immediately
            onSuccess.onSuccess(cachedEvents);
        }
        
        // Make API call (whether we returned cached data or not, to refresh cache)
        Call<PageResponse<Event>> call = apiService.filterEvents(
                new EventRequest() // Empty request for all events
        );
        
        call.enqueue(new ApiErrorHandler.ApiCallback<>(
                context,
                response -> {
                    // Cache the response
                    cacheManager.put(cacheKey, response);
                    
                    // Return result to caller if we didn't already return cached data
                    if (cachedEvents == null) {
                        onSuccess.onSuccess(response);
                    }
                },
                onError,
                "Failed to load events"
        ));
    }
    
    /**
     * Get all events (simplified version without pagination)
     * 
     * @param context Activity context
     * @param onSuccess Success callback
     * @param onError Error callback
     * 
     * @author Mert Uzun
     * @version 1.0
     * @since 2025-05-09
     */
    public void getAllEvents(
            Context context,
            ApiErrorHandler.OnSuccess<List<Event>> onSuccess,
            ApiErrorHandler.OnError onError) {
        
        // Try to get from cache first
        List<Event> cachedEvents = cacheManager.get(CACHE_KEY_EVENTS, List.class);
        
        if (cachedEvents != null) {
            // Return cached data immediately
            onSuccess.onSuccess(cachedEvents);
        }
        
        // Make API call (refresh cache)
        Call<List<Event>> call = apiService.getEvents(sessionManager.getAuthToken());
        
        call.enqueue(new ApiErrorHandler.ApiCallback<>(
                context,
                response -> {
                    // Cache response
                    cacheManager.put(CACHE_KEY_EVENTS, response);
                    
                    // Return result to caller if we didn't return cached data
                    if (cachedEvents == null) {
                        onSuccess.onSuccess(response);
                    }
                },
                onError,
                "Failed to load events"
        ));
    }
    
    /**
     * Join an event
     * 
     * @param context Activity context
     * @param eventId Event ID to join
     * @param onSuccess Success callback
     * @param onError Error callback
     * 
     * @author Mert Uzun
     * @version 1.0
     * @since 2025-05-09
     */
    public void joinEvent(
            Context context,
            Long eventId,
            ApiErrorHandler.OnSuccess<Void> onSuccess,
            ApiErrorHandler.OnError onError) {
        
        Call<Void> call = apiService.joinEvent(eventId, sessionManager.getAuthToken());
        
        call.enqueue(new ApiErrorHandler.ApiCallback<>(
                context,
                response -> {
                    // Invalidate caches since data has changed
                    invalidateEventCaches();
                    
                    // Call success callback
                    onSuccess.onSuccess(response);
                },
                onError,
                "Failed to join event"
        ));
    }
    
    /**
     * Withdraw from an event
     * 
     * @param context Activity context
     * @param eventId Event ID to withdraw from
     * @param onSuccess Success callback
     * @param onError Error callback
     * 
     * @author Mert Uzun
     * @version 1.0
     * @since 2025-05-09
     */
    public void withdrawFromEvent(
            Context context,
            Long eventId,
            ApiErrorHandler.OnSuccess<Void> onSuccess,
            ApiErrorHandler.OnError onError) {
        
        Call<Void> call = apiService.withdrawEvent(eventId, sessionManager.getAuthToken());
        
        call.enqueue(new ApiErrorHandler.ApiCallback<>(
                context,
                response -> {
                    // Invalidate caches since data has changed
                    invalidateEventCaches();
                    
                    // Call success callback
                    onSuccess.onSuccess(response);
                },
                onError,
                "Failed to withdraw from event"
        ));
    }
    
    /**
     * Invalidate all event-related caches
     * 
     * @author Mert Uzun
     * @version 1.0
     * @since 2025-05-09
     */
    private void invalidateEventCaches() {
        cacheManager.remove(CACHE_KEY_EVENTS);
        
        // Remove paginated caches (clear the memory cache entirely)
        cacheManager.clearAll();
    }
} 