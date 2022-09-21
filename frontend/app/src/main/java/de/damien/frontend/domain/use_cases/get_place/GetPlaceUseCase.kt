package de.damien.frontend.domain.use_cases.get_place

import de.damien.frontend.common.Resource
import de.damien.frontend.data.remote.dto.toPlace
import de.damien.frontend.domain.models.Place
import de.damien.frontend.domain.repositorys.FunnyPlacesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetPlaceUseCase @Inject constructor(
    private val repository: FunnyPlacesRepository
) {
    operator fun invoke(placeId: Long): Flow<Resource<Place>> = flow {
        try {
            emit(Resource.Loading())
            val place = repository.getPlaceById(placeId).toPlace()
            emit(Resource.Success(place))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach the server. Check your internet connection"))
        }
    }
}