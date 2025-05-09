package com.bilkom.utils;

import android.content.Context;
import android.util.LruCache;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Manages caching for the application with both memory and disk caching
 * Aims to fasten the performance of our application Bilkom
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-05-09
 */
public class CacheManager {
    private static final String CACHE_DIR = "data_cache";
    private static final int DEFAULT_MEMORY_CACHE_SIZE = 4 * 1024 * 1024; // 4MB for our basic development/demo goals
    private static final long DEFAULT_CACHE_EXPIRY = 10 * 60 * 1000; // 10 minutes, again for our basic development/demo goals
    
    private static CacheManager instance;
    private final LruCache<String, Object> memoryCache;
    private final File cacheDir;
    private final Gson gson;
    private final Executor diskIOExecutor;
    
    private CacheManager(Context context) {
        // Initialize memory cache
        memoryCache = new LruCache<>(DEFAULT_MEMORY_CACHE_SIZE);
        
        // Initialize disk cache directory
        cacheDir = new File(context.getCacheDir(), CACHE_DIR);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        
        gson = new Gson();
        diskIOExecutor = Executors.newSingleThreadExecutor();
    }
    
    public static synchronized CacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new CacheManager(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * Put an object in cache (both memory and disk)
     * 
     * @param key Cache key
     * @param value Object to cache
     * @param <T> Type of object
     * 
     * @author Mert Uzun
     * @version 1.0
     * @since 2025-05-09
     */
    public <T> void put(String key, T value) {
        // Put in memory cache
        memoryCache.put(key, value);
        
        // Also save to disk cache
        diskIOExecutor.execute(() -> {
            File cacheFile = new File(cacheDir, key);
            try (FileWriter writer = new FileWriter(cacheFile)) {
                CacheEntry<T> entry = new CacheEntry<>(value, System.currentTimeMillis());
                writer.write(gson.toJson(entry));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Get an object from cache (checks memory first for faster access, then disk)
     * 
     * @param key Cache key
     * @param classOfT Class of the cached object
     * @param <T> Type of object
     * @return The cached object or null if not found or expired
     * 
     * @author Mert Uzun
     * @version 1.0
     * @since 2025-05-09
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> classOfT) {
        // First check memory cache
        Object cachedObject = memoryCache.get(key);
        if (cachedObject != null && classOfT.isInstance(cachedObject)) {
            return (T) cachedObject;
        }
        
        // If not in memory, check disk cache
        File cacheFile = new File(cacheDir, key);
        if (cacheFile.exists()) {
            try (FileReader reader = new FileReader(cacheFile)) {
                CacheEntry<T> entry = gson.fromJson(reader, 
                        gson.getTypeAdapter(CacheEntry.class).getClass());
                
                // Check if cache entry has expired
                if (System.currentTimeMillis() - entry.timestamp <= DEFAULT_CACHE_EXPIRY) {
                    // Valid cache, store in memory for faster access next time
                    T value = entry.value;
                    memoryCache.put(key, value);
                    return value;
                } else {
                    // Expired, delete cache file
                    diskIOExecutor.execute(cacheFile::delete);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    /**
     * Clear all caches (both memory and disk)
     * 
     * @author Mert Uzun
     * @version 1.0
     * @since 2025-05-09
     */
    public void clearAll() {
        // Clear memory cache
        memoryCache.evictAll();
        
        // Clear disk cache
        diskIOExecutor.execute(() -> {
            File[] cacheFiles = cacheDir.listFiles();
            if (cacheFiles != null) {
                for (File file : cacheFiles) {
                    file.delete();
                }
            }
        });
    }
    
    /**
     * Remove a specific item from cache (both memory and disk)
     * 
     * @param key Cache key to remove
     * 
     * @author Mert Uzun
     * @version 1.0
     * @since 2025-05-09
     */
    public void remove(String key) {
        // Remove from memory cache
        memoryCache.remove(key);
        
        // Remove from disk cache
        diskIOExecutor.execute(() -> {
            File cacheFile = new File(cacheDir, key);
            if (cacheFile.exists()) {
                cacheFile.delete();
            }
        });
    }
    
    /**
     * Helper class to store cache entries with timestamp
     * 
     * @author Mert Uzun
     * @version 1.0
     * @since 2025-05-09
     */
    private static class CacheEntry<T> {
        final T value;
        final long timestamp;
        
        CacheEntry(T value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }
    }
} 