package de.damien.frontend.presentation.places_map

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.damien.frontend.common.Resource
import de.damien.frontend.domain.use_cases.get_places.GetPlacesUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PlaceListViewModel @Inject constructor(
    private val getPlacesUseCase: GetPlacesUseCase
) : ViewModel() {

    private val _state = mutableStateOf(PlaceListState())
    val state: State<PlaceListState> = _state

    init {
        getPlaces()
    }

    private fun getPlaces() {
        getPlacesUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = PlaceListState(places = result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _state.value =
                        PlaceListState(error = result.message ?: "An unexpected error occurred")
                }
                is Resource.Loading -> {
                    _state.value = PlaceListState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

}