package com.bilkom.android.network

import com.bilkom.android.network.models.*
import retrofit2.Response
import retrofit2.http.*

interface BilkomApiService {
    // User endpoints
    @GET("api/users")
    suspend fun getAllUsers(): Response<List<User>>
    
    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<User>
    
    @POST("api/users")
    suspend fun createUser(@Body user: User): Response<User>
    
    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body user: User): Response<User>
    
    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<Void>
    
    // Club endpoints
    @GET("api/clubs")
    suspend fun getAllClubs(): Response<List<Club>>
    
    @GET("api/clubs/{id}")
    suspend fun getClubById(@Path("id") id: Long): Response<Club>
    
    @POST("api/clubs")
    suspend fun createClub(@Body club: Club): Response<Club>
    
    @PUT("api/clubs/{id}")
    suspend fun updateClub(@Path("id") id: Long, @Body club: Club): Response<Club>
    
    @DELETE("api/clubs/{id}")
    suspend fun deleteClub(@Path("id") id: Long): Response<Void>
    
    // Event endpoints
    @GET("api/events")
    suspend fun getAllEvents(): Response<List<Event>>
    
    @GET("api/events/{id}")
    suspend fun getEventById(@Path("id") id: Long): Response<Event>
    
    @POST("api/events")
    suspend fun createEvent(@Body event: Event): Response<Event>
    
    @PUT("api/events/{id}")
    suspend fun updateEvent(@Path("id") id: Long, @Body event: Event): Response<Event>
    
    @DELETE("api/events/{id}")
    suspend fun deleteEvent(@Path("id") id: Long): Response<Void>
    
    // Emergency Alert endpoints
    @GET("api/emergency-alerts")
    suspend fun getAllEmergencyAlerts(): Response<List<EmergencyAlert>>
    
    @GET("api/emergency-alerts/{id}")
    suspend fun getEmergencyAlertById(@Path("id") id: Long): Response<EmergencyAlert>
    
    @POST("api/emergency-alerts")
    suspend fun createEmergencyAlert(@Body alert: EmergencyAlert): Response<EmergencyAlert>
    
    @PUT("api/emergency-alerts/{id}")
    suspend fun updateEmergencyAlert(@Path("id") id: Long, @Body alert: EmergencyAlert): Response<EmergencyAlert>
    
    @DELETE("api/emergency-alerts/{id}")
    suspend fun deleteEmergencyAlert(@Path("id") id: Long): Response<Void>
} 