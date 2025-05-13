package com.bilkom.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class WeatherIconUtils {
    // OpenWeatherMap icon codes to our drawable resources
    private static final String[][] ICON_MAPPINGS = {
        // Clear sky
        {"01d", "weather_clear"},
        {"01n", "weather_clear_night"},
        
        // Few clouds
        {"02d", "weather_partly_cloudy"},
        {"02n", "weather_partly_cloudy_night"},
        
        // Scattered clouds
        {"03d", "weather_cloudy"},
        {"03n", "weather_cloudy"},
        
        // Broken clouds
        {"04d", "weather_cloudy"},
        {"04n", "weather_cloudy"},
        
        // Rain
        {"09d", "weather_heavy_rain"},
        {"09n", "weather_heavy_rain"},
        
        // Light rain
        {"10d", "weather_light_rain"},
        {"10n", "weather_light_rain"},
        
        // Thunderstorm
        {"11d", "weather_thunderstorm"},
        {"11n", "weather_thunderstorm"},
        
        // Snow
        {"13d", "weather_snow"},
        {"13n", "weather_snow"},
        
        // Mist
        {"50d", "weather_fog"},
        {"50n", "weather_fog"},
        
        // Additional conditions
        {"09d", "weather_drizzle"},  // Drizzle
        {"09n", "weather_drizzle"},
        {"13d", "weather_sleet"},    // Sleet
        {"13n", "weather_sleet"},
        {"13d", "weather_hail"},     // Hail
        {"13n", "weather_hail"},
        {"50d", "weather_wind"},     // Wind
        {"50n", "weather_wind"},
        {"11d", "weather_tornado"},  // Tornado
        {"11n", "weather_tornado"},
        {"11d", "weather_hurricane"}, // Hurricane
        {"11n", "weather_hurricane"}
    };

    /**
     * Get the drawable resource ID for a weather icon code
     * @param context The context
     * @param iconCode The OpenWeatherMap icon code
     * @return The drawable resource ID
     */
    public static int getWeatherIconResourceId(Context context, String iconCode) {
        // Default fallback icon
        int defaultIcon = android.R.drawable.ic_menu_compass;
        
        if (iconCode == null) return defaultIcon;
        
        try {
            // Try to load a custom weather icon by name pattern
            int resourceId = context.getResources().getIdentifier(
                "ic_weather_" + iconCode.replace("-", "_"), 
                "drawable", 
                context.getPackageName());
                
            // If found, use it
            if (resourceId != 0) {
                return resourceId;
            }
            
            // Otherwise use standard Android icons as fallbacks
            if (iconCode.contains("clear") || iconCode.equals("01d") || iconCode.equals("01n")) {
                return android.R.drawable.ic_menu_day;
            } else if (iconCode.contains("cloud") || iconCode.startsWith("02") || iconCode.startsWith("03") || iconCode.startsWith("04")) {
                return android.R.drawable.ic_menu_compass;
            } else if (iconCode.contains("rain") || iconCode.startsWith("09") || iconCode.startsWith("10")) {
                return android.R.drawable.ic_menu_recent_history;
            } else if (iconCode.contains("thunder") || iconCode.startsWith("11")) {
                return android.R.drawable.ic_dialog_alert;
            } else if (iconCode.contains("snow") || iconCode.startsWith("13")) {
                return android.R.drawable.ic_menu_view;
            } else {
                return defaultIcon;
            }
        } catch (Exception e) {
            return defaultIcon;
        }
    }

    /**
     * Get the drawable for a weather icon code
     * @param context The context
     * @param iconCode The OpenWeatherMap icon code
     * @return The drawable
     */
    public static Drawable getWeatherIconDrawable(Context context, String iconCode) {
        int resourceId = getWeatherIconResourceId(context, iconCode);
        return context.getResources().getDrawable(resourceId, null);
    }

    /**
     * Get the resource name for an icon code
     * @param iconCode The OpenWeatherMap icon code
     * @return The resource name
     */
    private static String getResourceNameForIconCode(String iconCode) {
        for (String[] mapping : ICON_MAPPINGS) {
            if (mapping[0].equals(iconCode)) {
                return mapping[1];
            }
        }
        // Default to cloudy if icon code not found
        return "weather_cloudy";
    }
} 