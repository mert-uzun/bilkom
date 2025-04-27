package com.bilkom.dto;

/**
 * A generic class for API responses.
 * @param <T> The type of the data in the response.
 * @author Mert Uzun
 * @version 1.0.0
 */
public class ApiResponse<T> {
    private T data;
    private String message;
    private boolean success;

    /**
     * Default constructor for ApiResponse.
     * @author Mert Uzun
     * @version 1.0.0
     */
    public ApiResponse() {}

    /**
     * Constructor for ApiResponse.
     * @param data The data to be returned in the response.
     * @param message The message to be returned in the response.
     * @param success Whether the operation was successful.
     * @author Mert Uzun
     * @version 1.0.0
     */
    public ApiResponse(T data, String message, boolean success) {
        this.data = data;
        this.message = message;
        this.success = success;
    }

    /**
     * Constructor for ApiResponse without the data.
     * @param message The message to be returned in the response.
     * @param success Whether the operation was successful.
     * @author Mert Uzun
     * @version 1.0.0
     */
    public ApiResponse(String message, boolean success) {
        this(null, message, success);
    }
    
    // Methods for convenience

    /**
     * Creates a success response with the given data and message.
     * @param <T> The type of the data in the response.
     * @param data The data to be returned in the response.
     * @param message The message to be returned in the response.
     * @return A success response with the given data and message.
     * @author Mert Uzun
     * @version 1.0.0
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(data, message, true);
    }

    /**
     * Creates a success response with the given message buw without data.
     * @param <T> The type of the data in the response.
     * @param message The message to be returned in the response.
     * @return A success response with the given message.
     * @author Mert Uzun
     * @version 1.0.0
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(message, true);
    }

    /**
     * Creates an error response with the given message.
     * @param <T> The type of the data in the response.
     * @param message The message to be returned in the response.
     * @return An error response with the given message.
     * @author Mert Uzun
     * @version 1.0.0
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(message, false);
    }
    
    //GETTERS AND SETTERS
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
}