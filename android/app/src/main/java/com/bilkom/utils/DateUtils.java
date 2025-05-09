package com.bilkom.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for handling date formatting and parsing consistently across the app
 * 
 * @author Mert Uzun
 * @version 1.0
 * @since 2025-05-09
 */
public class DateUtils {
    private static final String TAG = "DateUtils";
    
    // Standard date format for backend communication (matching backend's expected format)
    public static final String API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    
    // User-facing date formats
    public static final String USER_DATE_FORMAT = "yyyy-MM-dd";
    public static final String USER_DATETIME_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String USER_FRIENDLY_DATE_FORMAT = "MMM d, yyyy";
    public static final String USER_FRIENDLY_DATETIME_FORMAT = "MMM d, yyyy HH:mm";
    
    /**
     * Parses a date string using the API date format
     * 
     * @param dateString Date string in API format
     * @return Parsed Date object or null if parsing fails
     */
    public static Date parseApiDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault());
            return sdf.parse(dateString);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing API date: " + dateString, e);
            
            // Try alternate format without time component
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(USER_DATE_FORMAT, Locale.getDefault());
                return sdf.parse(dateString);
            } catch (ParseException e2) {
                Log.e(TAG, "Error parsing alternate date format: " + dateString, e2);
                return null;
            }
        }
    }
    
    /**
     * Formats a Date object to API date format string
     * 
     * @param date Date to format
     * @return Formatted date string or empty string if date is null
     */
    public static String formatApiDate(Date date) {
        if (date == null) {
            return "";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }
    
    /**
     * Formats a Date object to user-friendly display format
     * 
     * @param date Date to format
     * @return Formatted date string or empty string if date is null
     */
    public static String formatUserFriendlyDate(Date date) {
        if (date == null) {
            return "";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(USER_FRIENDLY_DATE_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }
    
    /**
     * Formats a Date object to user-friendly display format with time
     * 
     * @param date Date to format
     * @return Formatted date string or empty string if date is null
     */
    public static String formatUserFriendlyDateTime(Date date) {
        if (date == null) {
            return "";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(USER_FRIENDLY_DATETIME_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }
    
    /**
     * Formats a Date object to ISO date format (YYYY-MM-DD)
     * 
     * @param date Date to format
     * @return Formatted date string or empty string if date is null
     */
    public static String formatIsoDate(Date date) {
        if (date == null) {
            return "";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(USER_DATE_FORMAT, Locale.getDefault());
        return sdf.format(date);
    }
    
    /**
     * Validates if a date string matches expected format (YYYY-MM-DD)
     * 
     * @param dateString Date string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidDateFormat(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return false;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(USER_DATE_FORMAT, Locale.getDefault());
            sdf.setLenient(false); // Strict parsing
            sdf.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    
    /**
     * Parse a user input date string (YYYY-MM-DD)
     * 
     * @param dateString Date string in user input format
     * @return Parsed Date object or null if parsing fails
     */
    public static Date parseUserInputDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(USER_DATE_FORMAT, Locale.getDefault());
            sdf.setLenient(false); // Strict parsing for user input
            return sdf.parse(dateString);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing user input date: " + dateString, e);
            return null;
        }
    }
    
    /**
     * Gets formatted date string from one format to another
     * 
     * @param dateString Input date string
     * @param inputFormat Format of the input string
     * @param outputFormat Desired output format
     * @return Formatted date string or empty string if conversion fails
     */
    public static String convertDateFormat(String dateString, String inputFormat, String outputFormat) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }
        
        try {
            SimpleDateFormat inputSdf = new SimpleDateFormat(inputFormat, Locale.getDefault());
            Date date = inputSdf.parse(dateString);
            
            if (date != null) {
                SimpleDateFormat outputSdf = new SimpleDateFormat(outputFormat, Locale.getDefault());
                return outputSdf.format(date);
            }
        } catch (ParseException e) {
            Log.e(TAG, "Error converting date format: " + dateString, e);
        }
        
        return "";
    }
} 