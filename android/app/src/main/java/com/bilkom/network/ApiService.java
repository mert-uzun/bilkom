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

    @POST("/api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest body);

    @POST("/api/auth/register")
    Call<AuthResponse> register(@Body RegistrationRequest body);

    // POST request to verify the email address
    // I'm not sure if this will work
    // Please check the actual code, there is a backend endpoint for this
    @POST("/api/auth/logout")
    Call<Void> logout(@Header("Authorization") String token);

    // I'm not sure if this will work
    // Please check the actual code, there is a backend endpoint for this
    @POST("/api/auth/reset-password/request")
    Call<Void> requestPasswordReset(@Body Map<String, String> payload);

    @GET("/api/users")
    Call<List<User>> listUsers();

    @POST("/api/users")
    Call<User> createUser(@Body User body);

    @GET("/api/users/{id}")
    Call<User> getUser(@Path("id") Long id);

    @PUT("/api/users/{id}")
    Call<User> updateUser(@Path("id") Long id, @Body User body);

    @DELETE("/api/users/{id}")
    Call<Void> deleteUser(@Path("id") Long id);

    @PUT("/api/users/{id}/active")
    Call<Void> setUserActive(@Path("id") Long id, @Body Map<String, Boolean> payload);

    @GET("/api/users/me/all-clubs")
    Call<List<Club>> getMyClubsAll();

    @GET("/api/users/{id}/all-clubs")
    Call<List<Club>> getUserClubsAll(@Path("id") Long id);

    @GET("/api/admin/users")
    Call<List<User>> adminListUsers();

    @PUT("/api/admin/users/{id}/verified")
    Call<Void> adminVerify(@Path("id") Long id);

    @GET("/api/clubs")
    Call<List<Club>> listClubs();

    @POST("/api/clubs")
    Call<Club> createClub(@Body ClubRequest body);

    @GET("/api/clubs/{id}")
    Call<Club> getClub(@Path("id") Long clubId, @Header("Authorization") String token);

    @PUT("/api/clubs/{id}")
    Call<Club> updateClub(@Path("id") Long id, @Body Club body);

    @DELETE("/api/clubs/{id}")
    Call<Void> deleteClub(@Path("id") Long id);

    @PUT("/api/clubs/{id}/reactivate")
    Call<Void> reactivateClub(@Path("id") Long id);

    @POST("/api/clubs/registration")
    Call<Club> registerClub(@Body ClubRequest body, @Header("Authorization") String token);

    @GET("/api/clubs/registration/pending/{adminId}")
    Call<List<Club>> getPendingRegistrations(@Path("adminId") Long adminId);

    @GET("/api/clubs/members/club/{clubId}")
    Call<List<ClubMember>> listMembers(@Path("clubId") Long clubId);

    @POST("/api/clubs/{clubId}/members")
    Call<Void> joinClub(@Path("clubId") Long clubId);

    @DELETE("/api/clubs/members/{userId}/club/{clubId}")    
    Call<Void> removeMember(@Path("clubId") Long clubId, @Path("userId") Long userId);

    @POST("/api/clubs/executives/club/{clubId}")
    Call<Void> addExecutive(@Path("clubId") Long clubId, @Body ClubMember body);

    @GET("/api/clubs/executives/club/{clubId}")
    Call<List<ClubMember>> listExecutives(@Path("clubId") Long clubId);

    @PUT("/api/clubs/executives/{userId}/club/{clubId}")
    Call<Void> changeExecutive(@Path("userId") Long userId, @Path("clubId") Long clubId);

    @GET("/api/events")
    Call<List<Event>> listEvents();

    @POST("/api/events")
    Call<Event> createEvent(@Body EventRequest body, @Header("Authorization") String token);

    @POST("/api/events/create-club-event ")    
    Call<Event> createClubEvent(@Body EventRequest body, @Header("Authorization") String token);

    @GET("/api/events/{id}")
    Call<Event> getEvent(@Path("id") Long id);

    @PUT("/api/events/{id}")
    Call<Event> updateEvent(@Path("id") Long id, @Body Event body);

    @DELETE("/api/events/{id}")
    Call<Void> deleteEvent(@Path("id") Long id);

    @GET("/api/events/created")
    Call<List<Event>> getMyCreatedEvents();

    @GET("/api/events/created/past")
    Call<List<Event>> getMyCreatedPast();

    @GET("/api/events/joined/past")
    Call<List<Event>> getMyJoinedPast();

    @GET("/api/events/my-club-events/past")
    Call<List<Event>> getPastOfMyClubs();

    @GET("/api/events/clubs/{clubId}/events")
    Call<List<Event>> getClubEvents(@Path("clubId") Long clubId);

    @GET("/api/events/clubs/{clubId}/events/current")
    Call<List<Event>> getClubCurrentEvents(@Path("clubId") Long clubId);

    @POST("/api/events/filter")
    Call<PageResponse<Event>> filterEvents(@Body EventRequest body);

    /*@POST("/events/filter/paged")
    Call<PageResponse<Event>> filterEventsPaged(
        @Body EventRequest body,
        @Query("page") int page,
        @Query("size") int size
    );*/ 
    // I didn't understand the purpose of this method
    // There is no such method in the backend

    @POST("/api/events/{eventId}/join")
    Call<Void> joinEvent(@Path("eventId") Long eventId, @Header("Authorization") String token);

    @POST("/api/events/{eventId}/withdraw")
    Call<Void> withdrawEvent(@Path("eventId") Long eventId, @Header("Authorization") String token);

    @POST("/api/events/{eventId}/report")
    Call<Void> reportEvent(@Path("eventId") Long eventId, @Body ReportRequest body);

    @GET("/weather")
    Call<WeatherForecast> getWeather();

    @GET("/news")
    Call<List<News>> getNews();

    @GET("/api/emergency-alerts")
    Call<List<EmergencyAlert>> getAlerts();

    /*@GET("/emergency-alerts/paged")
    Call<PageResponse<EmergencyAlert>> getAlertsPaged(
        @Query("page") int page,
        @Query("size") int size
    );*/
    // I didn't understand the purpose of this method
    // There is no such method in the backend

    @GET("/api/users/me/clubs")
    Call<List<Club>> getMyClubs(@Header("Authorization") String token);

    @GET("/api/events/clubs/{clubId}/events")
    Call<List<Event>> getClubEventsByClubId(@Path("clubId") long clubId, @Header("Authorization") String token);

    @GET("/api/events/club/my-club-events")
    Call<Map<Long, List<Event>>> getMyClubsEvents(@Header("Authorization") String token);
    @POST("/api/clubs/{id}/approve")
    Call<Void> approveClub(@Path("id") Long id);

    @POST("/api/clubs/{id}/reject") 
    Call<Void> rejectClub(@Path("id") Long id);

    @GET("/weather/forecast")
    Call<WeatherForecast> getWeatherForecast();

    @GET("/news/latest")
    Call<List<News>> getLatestNews();

    @GET("/api/events")
    Call<List<Event>> getEvents(@Header("Authorization") String token);

    @GET("/api/tags")
    Call<List<String>> getAvailableTags();

    @GET("/api/events/joined")
    Call<List<Event>> getJoinedEvents(@Header("Authorization") String token);

    @POST("/api/events/filter/tags")
    Call<List<Event>> filterEventsByTags(@Body List<String> tags, @Header("Authorization") String token);
    // here, the /events/filter already filters by tags in the backend, but probably takes tags as a parameter. 
    // so i believe this should work, check again
}