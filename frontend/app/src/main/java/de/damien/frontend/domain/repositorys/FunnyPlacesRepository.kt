package de.damien.frontend.domain.repositorys

import de.damien.frontend.data.remote.dto.PlaceDto

interface FunnyPlacesRepository {

    suspend fun getPlaces(): List<PlaceDto>

    suspend fun getPlaceById(placeId: Long): PlaceDto

}