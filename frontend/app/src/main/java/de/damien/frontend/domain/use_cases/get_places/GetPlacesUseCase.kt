package de.damien.frontend.domain.use_cases.get_places

import de.damien.frontend.common.Resource
import de.damien.frontend.data.remote.dto.toPlace
import de.damien.frontend.domain.models.Place
import de.damien.frontend.domain.repositorys.FunnyPlacesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetPlacesUseCase @Inject constructor(
    private val repository: FunnyPlacesRepository
) {
    operator fun invoke(): Flow<Resource<List<Place>>> = flow {
        try {
            emit(Resource.Loading())
            val places = repository.getPlaces().map { it.toPlace() }
            emit(Resource.Success(places))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach the server. Check your internet connection"))
        }
    }
}