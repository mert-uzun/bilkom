//this is the api service interface for the api calls
// it is used to store the api calls for the login, register, get user by id, get user by email and get user by bilkentId
package com.bilkom.network;

import com.bilkom.model.LoginRequest;
import com.bilkom.model.RegistrationRequest;
import com.bilkom.model.AuthResponse;
import com.bilkom.model.User;
import com.bilkom.model.EmergencyAlert;
import com.bilkom.model.WeatherForecast;
import com.bilkom.model.Event;
import com.bilkom.model.EventRequest;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegistrationRequest registrationRequest);

    @GET("users/{id}")
    Call<User> getUserById(
        @Path("id") Long id,
        @Header("Authorization") String bearerToken
    );

    @PUT("users/{id}")
    Call<User> updateUser(
        @Path("id") Long id,
        @Body User user,
        @Header("Authorization") String bearerToken
    );

    @GET("users/email/{email}")
    Call<User> getUserByEmail(
        @Path("email") String email,
        @Header("Authorization") String bearerToken
    );

    @GET("users/bilkentId/{bilkentId}")
    Call<User> getUserByBilkentId(
        @Path("bilkentId") String bilkentId,
        @Header("Authorization") String bearerToken
    );

    @GET("emergency-alerts")
    Call<List<EmergencyAlert>> getEmergencyAlerts(
        @Header("Authorization") String bearerToken
    );

    @GET("weather")
    Call<WeatherForecast> getWeatherForecast();

    @GET("news")
    Call<List<com.bilkom.model.News>> getLatestNews();

    @GET("events")
    Call<List<Event>> getEvents(@Header("Authorization") String bearerToken);

    @POST("events")
    Call<Event> createEvent(@Body EventRequest eventRequest, @Header("Authorization") String bearerToken);

    @POST("events/filter")
    Call<List<Event>> filterEventsByTags(@Body List<String> tagNames, @Header("Authorization") String bearerToken);

}