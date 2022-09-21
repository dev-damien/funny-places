package de.damien.frontend.data.repositorys

import de.damien.frontend.data.remote.FunnyPlacesApi
import de.damien.frontend.data.remote.dto.PlaceDto
import de.damien.frontend.domain.repositorys.FunnyPlacesRepository
import javax.inject.Inject

class FunnyPlacesRepositoryImpl @Inject constructor(
    private val api: FunnyPlacesApi
) : FunnyPlacesRepository {

    override suspend fun getPlaces(): List<PlaceDto> {
        return api.getPlaces()
    }

    override suspend fun getPlaceById(placeId: Long): PlaceDto {
        return api.getPlaceById(placeId)
    }

}