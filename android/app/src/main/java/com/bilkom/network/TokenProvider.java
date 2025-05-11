package com.bilkom.network;

/**
 * A utility class for managing a token in a thread-safe manner.
 * This class provides methods to save, retrieve, and clear a token.
 * 
 * <p>Note: This class is designed to store the token in memory and 
 * does not provide any persistence mechanism.</p>
 * 
 * @author Sıla Bozkurt
 */
public final class TokenProvider {

    private static volatile String token;

    private TokenProvider() { }

    public static void saveToken(String jwt) { token = jwt; }

    public static String getToken() { return token; }

    public static void clear() { token = null; }
}
