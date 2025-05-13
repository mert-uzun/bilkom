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

    // POST request to verify the email address
    // I'm not sure if this will work
    // Please check the actual code, there is a backend endpoint for this
    @POST("/auth/logout")
    Call<Void> logout(@Header("Authorization") String token);

    // I'm not sure if this will work
    // Please check the actual code, there is a backend endpoint for this
    @POST("/auth/reset-password/request")
    Call<Void> requestPasswordReset(@Body PasswordResetRequest body);

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
    Call<Club> registerClub(@Body ClubRequest body);

    @GET("/clubs/registration/pending/{adminId}")
    Call<List<Club>> getPendingRegistrations(@Path("adminId") Long adminId);

    @GET("/clubs/members/club/{clubId}")
    Call<List<ClubMember>> listMembers(@Path("clubId") Long clubId);

    @POST("/clubs/{clubId}/members")
    Call<Void> joinClub(@Path("clubId") Long clubId);

    @DELETE("/clubs/members/{userId}/club/{clubId}")    
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

    @POST("/events/create-club-event ")    
    Call<Event> createClubEvent(@Body EventRequest body, @Header("Authorization") String token);

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

    /*@POST("/events/filter/paged")
    Call<PageResponse<Event>> filterEventsPaged(
        @Body EventRequest body,
        @Query("page") int page,
        @Query("size") int size
    );*/ 
    // I didn't understand the purpose of this method
    // There is no such method in the backend

    @POST("/events/{eventId}/join")
    Call<Void> joinEvent(@Path("eventId") Long eventId, @Header("Authorization") String token);

    @POST("/events/{eventId}/withdraw")
    Call<Void> withdrawEvent(@Path("eventId") Long eventId, @Header("Authorization") String token);

    @POST("/events/{eventId}/report")
    Call<Void> reportEvent(@Path("eventId") Long eventId, @Body ReportRequest body);

    @GET("/weather")
    Call<WeatherForecast> getWeather();

    @GET("/news")
    Call<List<News>> getNews();

    @GET("/emergency-alerts")
    Call<List<EmergencyAlert>> getAlerts();

    /*@GET("/emergency-alerts/paged")
    Call<PageResponse<EmergencyAlert>> getAlertsPaged(
        @Query("page") int page,
        @Query("size") int size
    );*/
    // I didn't understand the purpose of this method
    // There is no such method in the backend

    @GET("clubs/my")
    Call<List<Club>> getMyClubs(@Header("Authorization") String token);

    @GET("events/club/{clubId}")
    Call<List<Event>> getClubEventsByClubId(@Path("clubId") long clubId, @Header("Authorization") String token);

    @GET("events/club/my-club-events")
    Call<Map<Long, List<Event>>> getMyClubsEvents(@Header("Authorization") String token);
    @POST("/clubs/{id}/approve")
    Call<Void> approveClub(@Path("id") Long id);

    @POST("/clubs/{id}/reject") 
    Call<Void> rejectClub(@Path("id") Long id);

    /*@GET("weather")
    Call<WeatherForecast> getWeatherForecast();*/
    /*we cant get forecast cause we dont pay for the api */

    /*@GET("news")
    Call<List<News>> getLatestNews();*/ 
    /*we dont need get latest news we already always fetch latest news */

    @GET("/events")
    Call<List<Event>> getEvents(@Header("Authorization") String token);

    @GET("/events/joined")
    Call<List<Event>> getJoinedEvents(@Header("Authorization") String token);

    @POST("/events/filter/tags")
    Call<List<Event>> filterEventsByTags(@Body List<String> tags, @Header("Authorization") String token);
    // here, the /events/filter already filters by tags in the backend, but probably takes tags as a parameter. 
    // so i believe this should work, check again
}