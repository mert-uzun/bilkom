package com.bilkom.network;

import com.bilkom.model.*;
import java.util.*;
import okhttp3.*;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * ApiService interface defines the REST API endpoints for the application.
 * It includes methods for user authentication, user management, club management,
 * event management, and other related operations.
 * 
 * 
 * @author Sıla Bozkurt
 */
public interface ApiService {

    @POST("/auth/login")
    Call<AuthResponse> login(@Body LoginRequest body);

    @POST("/auth/register")
    Call<AuthResponse> register(@Body RegistrationRequest body);

    @GET("/users")
    Call<List<User>> listUsers();

    @POST("/users")
    Call<User> createUser(@Body User body);

    @GET("/users/{id}")
    Call<User> getUser(@Path("id") Long id);

    @PUT("/users/{id}")
    Call<User> updateUser(@Path("id") Long id, @Body User body);

    @DELETE("/users/{id}")
    Call<Void> deleteUser(@Path("id") Long id);

    @PUT("/users/{id}/active")
    Call<Void> setUserActive(@Path("id") Long id, @Body Map<String, Boolean> payload);

    @GET("/users/me/all-clubs")
    Call<List<Club>> getMyClubsAll();

    @GET("/users/{id}/all-clubs")
    Call<List<Club>> getUserClubsAll(@Path("id") Long id);

    @GET("/admin/users")
    Call<List<User>> adminListUsers();

    @PUT("/admin/users/{id}/verified")
    Call<Void> adminVerify(@Path("id") Long id);

    @GET("/clubs")
    Call<List<Club>> listClubs();

    @POST("/clubs")
    Call<Club> createClub(@Body ClubRequest body);

    @GET("/clubs/{id}")
    Call<Club> getClub(@Path("id") Long id);

    @PUT("/clubs/{id}")
    Call<Club> updateClub(@Path("id") Long id, @Body Club body);

    @DELETE("/clubs/{id}")
    Call<Void> deleteClub(@Path("id") Long id);

    @PUT("/clubs/{id}/reactivate")
    Call<Void> reactivateClub(@Path("id") Long id);

    @POST("/clubs/registration")
    Call<ClubRegistrationRequest> registerClub(@Body ClubRegistrationRequest body);

    @GET("/clubs/registration/pending/{adminId}")
    Call<List<ClubRegistrationRequest>> getPendingRegistrations(@Path("adminId") Long adminId);

    @GET("/clubs/members/club/{clubId}")
    Call<List<ClubMember>> listMembers(@Path("clubId") Long clubId);

    @POST("/clubs/{clubId}/members")
    Call<Void> joinClub(@Path("clubId") Long clubId);

    @DELETE("/clubs/{clubId}/members/{userId}")
    Call<Void> removeMember(@Path("clubId") Long clubId, @Path("userId") Long userId);

    @POST("/clubs/executives/club/{clubId}")
    Call<Void> addExecutive(@Path("clubId") Long clubId, @Body ClubMember body);

    @GET("/clubs/executives/club/{clubId}")
    Call<List<ClubMember>> listExecutives(@Path("clubId") Long clubId);

    @PUT("/clubs/executives/{userId}/club/{clubId}")
    Call<Void> changeExecutive(@Path("userId") Long userId, @Path("clubId") Long clubId);

    @GET("/events")
    Call<List<Event>> listEvents();

    @POST("/events")
    Call<Event> createEvent(@Body EventRequest body, @Header("Authorization") String token);

    @GET("/events/{id}")
    Call<Event> getEvent(@Path("id") Long id);

    @PUT("/events/{id}")
    Call<Event> updateEvent(@Path("id") Long id, @Body Event body);

    @DELETE("/events/{id}")
    Call<Void> deleteEvent(@Path("id") Long id);

    @GET("/events/created")
    Call<List<Event>> getMyCreatedEvents();

    @GET("/events/created/past")
    Call<List<Event>> getMyCreatedPast();

    @GET("/events/joined/past")
    Call<List<Event>> getMyJoinedPast();

    @GET("/events/my-club-events/past")
    Call<List<Event>> getPastOfMyClubs();

    @GET("/events/clubs/{clubId}/events")
    Call<List<Event>> getClubEvents(@Path("clubId") Long clubId);

    @GET("/events/clubs/{clubId}/events/current")
    Call<List<Event>> getClubCurrentEvents(@Path("clubId") Long clubId);

    @POST("/events/filter")
    Call<PageResponse<Event>> filterEvents(@Body EventRequest body);

    @POST("/events/filter/paged")
    Call<PageResponse<Event>> filterEventsPaged(
        @Body EventRequest body,
        @Query("page") int page,
        @Query("size") int size
    );

    @POST("/events/{eventId}/join")
    Call<Void> joinEvent(@Path("eventId") Long eventId);

    @POST("/events/{eventId}/withdraw")
    Call<Void> withdrawEvent(@Path("eventId") Long eventId);

    @POST("/events/report/{eventId}")
    Call<Void> reportEvent(@Path("eventId") Long eventId, @Body ReportRequest body);

    @GET("weather")
    Call<WeatherForecast> getWeather();

    @GET("news")
    Call<List<News>> getNews();

    @GET("/emergency-alerts")
    Call<List<EmergencyAlert>> getAlerts();

    @GET("/emergency-alerts/paged")
    Call<PageResponse<EmergencyAlert>> getAlertsPaged(
        @Query("page") int page,
        @Query("size") int size
    );

    @GET("/")
    Call<ApiResponse> ping();

    @POST("/clubs/{id}/approve")
    Call<Void> approveClub(@Path("id") Long id);

    @POST("/clubs/{id}/reject") 
    Call<Void> rejectClub(@Path("id") Long id);

    @GET("weather")
    Call<WeatherForecast> getWeatherForecast();

    @GET("news")
    Call<List<News>> getLatestNews();
}