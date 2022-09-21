package de.damien.frontend.data.remote

import de.damien.frontend.data.remote.dto.PlaceDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FunnyPlacesApi {

    @POST("/api/v1/login")
    suspend fun login(): String

    @GET("/api/v1/places")
    suspend fun getPlaces(): List<PlaceDto>

    @GET("/api/v1/places/{placeId}")
    suspend fun getPlaceById(@Path("placeId") placeId: Long): PlaceDto



}