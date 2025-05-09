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
import com.bilkom.model.Club;
import com.bilkom.model.ReportRequest;
import com.bilkom.model.ClubRequest;
import com.bilkom.model.ClubMembershipRequest;
import com.bilkom.model.ClubMember;
import com.bilkom.model.PageResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @GET("emergency-alerts/paged")
    Call<PageResponse<EmergencyAlert>> getPagedEmergencyAlerts(
        @Query("page") int page,
        @Query("size") int size,
        @Header("Authorization") String bearerToken
    );

    @GET("weather")
    Call<WeatherForecast> getWeatherForecast();

    @GET("news")
    Call<List<com.bilkom.model.News>> getLatestNews();

    @GET("news/paged")
    Call<PageResponse<com.bilkom.model.News>> getPagedNews(
        @Query("page") int page,
        @Query("size") int size
    );

    @GET("events")
    Call<List<Event>> getEvents(@Header("Authorization") String bearerToken);

    @GET("events/paged")
    Call<PageResponse<Event>> getPagedEvents(
        @Query("page") int page,
        @Query("size") int size,
        @Header("Authorization") String bearerToken
    );

    @POST("events")
    Call<Event> createEvent(@Body EventRequest eventRequest, @Header("Authorization") String bearerToken);

    @POST("events/filter")
    Call<List<Event>> filterEventsByTags(@Body List<String> tagNames, @Header("Authorization") String bearerToken);

    @POST("events/filter/paged")
    Call<PageResponse<Event>> filterPagedEventsByTags(
        @Body List<String> tagNames,
        @Query("page") int page,
        @Query("size") int size,
        @Header("Authorization") String bearerToken
    );

    @GET("events/clubs/{clubId}/events/current")
    Call<List<Event>> getClubCurrentEvents(@Path("clubId") Long clubId, @Header("Authorization") String token);

    @GET("events/clubs/{clubId}/events/current/paged")
    Call<PageResponse<Event>> getPagedClubCurrentEvents(
        @Path("clubId") Long clubId,
        @Query("page") int page,
        @Query("size") int size,
        @Header("Authorization") String token
    );

    @GET("events/joined")
    Call<List<Event>> getJoinedEvents(@Header("Authorization") String token);

    @GET("events/joined/paged")
    Call<PageResponse<Event>> getPagedJoinedEvents(
        @Query("page") int page,
        @Query("size") int size,
        @Header("Authorization") String token
    );

    @GET("events/my-club-events/current")
    Call<Map<Long, List<Event>>> getMyClubsEvents(@Header("Authorization") String token);

    @GET("events/clubs/current")
    Call<List<Event>> getAllClubEvents(@Header("Authorization") String token);

    @GET("events/clubs/current/paged")
    Call<PageResponse<Event>> getPagedAllClubEvents(
        @Query("page") int page,
        @Query("size") int size,
        @Header("Authorization") String token
    );

    @POST("events/{eventId}/join")
    Call<Void> joinEvent(@Path("eventId") Long eventId, @Header("Authorization") String bearerToken);

    @POST("events/{eventId}/withdraw")
    Call<Void> withdrawFromEvent(@Path("eventId") Long eventId, @Header("Authorization") String bearerToken);

    @GET("user-settings/profile/{id}")
    Call<User> getProfile(
        @Path("id") Long userId,
        @Header("Authorization") String bearerToken
    );

    @PUT("user-settings/profile/{id}")
    Call<Void> updateProfile(
        @Path("id") Long userId,
        @Body User user,
        @Header("Authorization") String bearerToken
    );

    @Multipart
    @PUT("user-settings/avatar/{id}")
    Call<Void> updateAvatar(
        @Path("id") Long userId,
        @Part MultipartBody.Part avatarImage,
        @Header("Authorization") String bearerToken
    );

    @POST("user-settings/change-password/{id}")
    Call<Void> changePassword(
        @Path("id") Long userId,
        @Body Map<String, String> passwordMap,
        @Header("Authorization") String bearerToken
    );

    @POST("user-settings/logout/{id}")
    Call<Void> logout(
        @Path("id") Long userId,
        @Header("Authorization") String bearerToken
    );

    @POST("events/report/{eventId}")
    Call<Void> reportPastEvent(
        @Path("eventId") Long eventId,
        @Body ReportRequest reportRequest,
        @Header("Authorization") String bearerToken
    );

    @GET("events/clubs/{clubId}/events")
    Call<List<Event>> getAllClubEvents(
        @Path("clubId") Long clubId,
        @Header("Authorization") String token
    );

    @GET("events/clubs/{clubId}/events/paged")
    Call<PageResponse<Event>> getPagedClubEvents(
        @Path("clubId") Long clubId,
        @Query("page") int page,
        @Query("size") int size,
        @Header("Authorization") String token
    );

    @POST("events/{eventId}/cancel")
    Call<Void> cancelClubEvent(
        @Path("eventId") Long eventId,
        @Header("Authorization") String token
    );

    @GET("clubs")
    Call<List<Club>> getAllClubs(@Header("Authorization") String token);
    
    @GET("clubs/paged")
    Call<PageResponse<Club>> getPagedClubs(
        @Query("page") int page,
        @Query("size") int size,
        @Header("Authorization") String token
    );
    
    @GET("clubs/{clubId}")
    Call<Club> getClubById(
        @Path("clubId") Long clubId,
        @Header("Authorization") String token
    );
    
    @POST("clubs")
    Call<Club> createClub(
        @Body ClubRequest clubRequest,
        @Header("Authorization") String token
    );
    
    @PUT("clubs/{clubId}")
    Call<Club> updateClub(
        @Path("clubId") Long clubId,
        @Body Club club,
        @Header("Authorization") String token
    );
    
    @DELETE("clubs/{clubId}")
    Call<Void> deleteClub(
        @Path("clubId") Long clubId,
        @Header("Authorization") String token
    );
    
    @GET("users/me/clubs")
    Call<List<Club>> getMyClubs(@Header("Authorization") String token);
    
    @GET("clubs/{clubId}/members")
    Call<List<ClubMember>> getClubMembers(
        @Path("clubId") Long clubId,
        @Header("Authorization") String token
    );
    
    @GET("clubs/{clubId}/members/paged")
    Call<PageResponse<ClubMember>> getPagedClubMembers(
        @Path("clubId") Long clubId,
        @Query("page") int page,
        @Query("size") int size,
        @Header("Authorization") String token
    );
    
    @POST("clubs/{clubId}/members")
    Call<Void> joinClub(
        @Path("clubId") Long clubId,
        @Header("Authorization") String token
    );
    
    @DELETE("clubs/{clubId}/members/{userId}")
    Call<Void> removeClubMember(
        @Path("clubId") Long clubId,
        @Path("userId") Long userId,
        @Header("Authorization") String token
    );
    
    @GET("clubs/{clubId}/membership-requests")
    Call<List<ClubMembershipRequest>> getClubMembershipRequests(
        @Path("clubId") Long clubId,
        @Header("Authorization") String token
    );
    
    @GET("clubs/{clubId}/membership-requests/paged")
    Call<PageResponse<ClubMembershipRequest>> getPagedClubMembershipRequests(
        @Path("clubId") Long clubId,
        @Query("page") int page,
        @Query("size") int size,
        @Header("Authorization") String token
    );
    
    @POST("clubs/{clubId}/membership-requests")
    Call<Void> requestClubMembership(
        @Path("clubId") Long clubId,
        @Header("Authorization") String token
    );
    
    @PUT("clubs/{clubId}/membership-requests/{requestId}/approve")
    Call<Void> approveClubMembershipRequest(
        @Path("clubId") Long clubId,
        @Path("requestId") Long requestId,
        @Header("Authorization") String token
    );
    
    @PUT("clubs/{clubId}/membership-requests/{requestId}/reject")
    Call<Void> rejectClubMembershipRequest(
        @Path("clubId") Long clubId,
        @Path("requestId") Long requestId,
        @Header("Authorization") String token
    );
}