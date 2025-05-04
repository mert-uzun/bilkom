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
import java.util.Map;
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

    @GET("events/clubs/{clubId}/events/current")
    Call<List<Event>> getClubEventsByClubId(@Path("clubId") Long clubId, @Header("Authorization") String token);

    @GET("events/joined")
    Call<List<Event>> getJoinedEvents(@Header("Authorization") String token);

    @GET("events/my-club-events/current")
    Call<Map<Long, List<Event>>> getMyClubsEvents(@Header("Authorization") String token);

    @GET("events/clubs/current")
    Call<List<Event>> getAllClubEvents(@Header("Authorization") String token);

    @POST("events/{eventId}/join")
    Call<Void> joinEvent(@Path("eventId") Long eventId, @Header("Authorization") String bearerToken);

    @POST("events/{eventId}/withdraw")
    Call<Void> withdrawFromEvent(@Path("eventId") Long eventId, @Header("Authorization") String bearerToken);

    @GET("user-settings/profile/{id}")
    Call<User> getProfile(@Path("id") Long userId);

    @PUT("user-settings/profile/{id}")
    Call<Void> updateProfile(@Path("id") Long userId, @Body User user);

    @PUT("user-settings/avatar/{id}")
    Call<Void> updateAvatar(@Path("id") Long userId, @Body RequestBody avatarImage);

    @POST("user-settings/change-password/{id}")
    Call<Void> changePassword(@Path("id") Long userId, @Body Map<String, String> passwordMap);

    @POST("user-settings/logout/{id}")
    Call<Void> logout(@Path("id") Long userId);

    @POST("events/report/{eventId}")
    Call<Void> reportPastEvent(@Path("eventId") Long eventId, @Body String reason);

    @GET("events/clubs/{clubId}/events")
    Call<List<Event>> getClubEventsByClubId(@Path("clubId") Long clubId, @Header("Authorization") String token);

    @GET("users/me/clubs")
    Call<List<Club>> getMyClubs(@Header("Authorization") String token);

    @POST("events/{eventId}/cancel")
    Call<Void> cancelClubEvent(@Path("eventId") String eventId, @Header("Authorization") String token);

}